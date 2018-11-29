package com.gitlab.pedrioko.core.view.viewers.crud.grid;

import com.gitlab.pedrioko.core.view.api.CrudDisplayTable;
import com.gitlab.pedrioko.core.view.api.CrudGridItem;
import com.gitlab.pedrioko.core.view.api.OnEvent;
import com.gitlab.pedrioko.core.view.enums.CrudEvents;
import com.gitlab.pedrioko.core.view.util.ApplicationContextUtils;
import com.gitlab.pedrioko.core.view.util.ZKUtil;
import com.gitlab.pedrioko.services.StorageService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
public class CrudGrid extends Borderlayout implements CrudDisplayTable {

    private static final long serialVersionUID = 1L;
    private final transient List listitems = new ArrayList();
    private final Map<CrudEvents, List<OnEvent>> onEvent = new HashMap<>();
    private Columns columns;
    private Rows rows;
    private Class<?> klass;
    private Object selectValue;
    private String selectValueUUID = "";
    private StorageService storageService;
    private int cellsInRow = 4;
    private Grid grid = new Grid();
    private @Getter
    Long imageHeight;
    private int PAGE_SIZE = 16;
    private Paging paging;

    public CrudGrid(Class<?> klass) {
        super();
        this.klass = klass;
        init(klass);
    }


    public CrudGrid(Class<?> klass, List<Class<?>> all) {
        super();
        listitems.clear();
        listitems.addAll(all);
        init(klass);
    }

    private void init(Class<?> klass) {
        if (CrudGridItem.class.isAssignableFrom(klass)) {

            columns = new Columns();
            columns.setParent(grid);
            rows = new Rows();
            rows.setParent(grid);
            columns.appendChild(new Column(""));
            grid.appendChild(columns);
            grid.appendChild(rows);
            this.klass = klass;
            setVflex("1");
            setHflex("1");
            setStyle("height:100%; width:100%; overflow-y:auto !important;");
            grid.setStyle("width:100%;");
            rows.setHeight("135px");
            storageService = ApplicationContextUtils.getBean(StorageService.class);
            imageHeight = 100L;
            paging = new Paging();
            paging.setDetailed(true);
            if (ZKUtil.isMobile())
                paging.setMold("os");
            paging.setPageSize(PAGE_SIZE);
            paging.addEventListener("onPaging", (Event event) -> {
                PagingEvent pe = (PagingEvent) event;
                int pgno = pe.getActivePage();
                int ofs = pgno * PAGE_SIZE;
                redraw(ofs, PAGE_SIZE);
            });
            Center center = new Center();
            center.appendChild(grid);
            center.setStyle("overflow-y:auto !important;");
            this.appendChild(center);
            South south = new South();
            south.appendChild(paging);
            this.appendChild(south);
        } else {
            throw new IllegalArgumentException("Class " + klass + " not implement interface CrudGridItem");
        }

    }

    private void redraw(int firstResult, int maxResults) {
        if (listitems != null) {
            cellsInRow = ZKUtil.isMobile() ? 1 : cellsInRow;
            int fm = firstResult + maxResults;
            List page = listitems.subList(firstResult, listitems.size() < fm ?
                    listitems.size() : fm);
            grid.getRows().getChildren().clear();
            int counter = 0;
            Row row = new Row();
            int size = page.size();
            for (int i = 0; i < size; i++) {
                CrudGridItem obj = (CrudGridItem) page.get(i);
                GridItem child = new GridItem(obj, imageHeight);
                int finalI = i + firstResult;

                child.addEventListener(Events.ON_CLICK, (e) -> {
                    onClick(child, finalI);
                    if (ZKUtil.isMobile())
                        getEvent(CrudEvents.ON_RIGHT_CLICK).forEach(OnEvent::doSomething);
                });
                child.addEventListener(Events.ON_RIGHT_CLICK, (e) -> {
                    onClick(child, finalI);
                    getEvent(CrudEvents.ON_RIGHT_CLICK).forEach(OnEvent::doSomething);
                });
                Div div = new Div();
                div.appendChild(child);
                rows.addEventListener(Events.ON_RIGHT_CLICK, (e) -> {
                    onClick(child, finalI);
                    getEvent(CrudEvents.ON_RIGHT_CLICK).forEach(OnEvent::doSomething);
                });

                row.appendChild(div);
                row.setHeight("auto");
                counter++;

                if (counter == this.cellsInRow || (size < this.cellsInRow && counter == size) || (i == size - 1)) {
                    rows.appendChild(row);
                    row = new Row();
                    counter = 0;
                }
            }
        }
    }


    private void onClick(Vlayout child, int finalI) {
        selectValue = listitems.get(finalI);
        if (!selectValueUUID.isEmpty())
            Clients.evalJavaScript("var var1 = document.getElementById('" + selectValueUUID + "');" +
                    "if(var1 != null) {" +
                    "var1.style.border= '0px';" +
                    "}");


        Clients.evalJavaScript("document.getElementById('" + child.getUuid() + "').style.border=  '1px solid';" +
                "");

        selectValueUUID = child.getUuid();
    }

    private List<OnEvent> getEvent(CrudEvents events) {
        List<OnEvent> onEvents = this.onEvent.get(CrudEvents.ON_RIGHT_CLICK);
        return onEvents == null ? new ArrayList<>() : onEvents;
    }

    @Override
    public <T> T getSelectedValue() {
        return (T) selectValue;
    }

    @Override
    public void clearSelection() {
        selectValue = null;
    }

    @Override
    public List getValue() {
        return listitems;
    }

    @Override
    public void setValue(List<?> all) {
        listitems.clear();
        listitems.addAll(all);
        paging.setTotalSize(all.size());
        paging.setPageSize(PAGE_SIZE);
        update();
    }


    public void addEventOnEvent(CrudEvents e, OnEvent o) {
        List<OnEvent> onEvents = this.onEvent.get(e);
        if (onEvents == null) {
            onEvents = new ArrayList<>();
        }
        onEvents.add(o);
        this.onEvent.put(e, onEvents);
    }

    @Override
    public void update() {
        paging.setTotalSize(listitems.size());
        paging.setPageSize(PAGE_SIZE);
        redraw(paging.getActivePage() * PAGE_SIZE, PAGE_SIZE);
    }

    public void setPageSize(int pagesize) {
        this.PAGE_SIZE = pagesize;
        update();
    }

    public void setImageHeight(long imageHeight) {
        this.imageHeight = imageHeight;
        update();
    }

}