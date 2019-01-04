package com.gitlab.pedrioko.core.view.viewers.crud;

import com.gitlab.pedrioko.core.view.action.api.Action;
import com.gitlab.pedrioko.core.view.action.event.CrudActionEvent;
import com.gitlab.pedrioko.core.view.api.CrudDisplayTable;
import com.gitlab.pedrioko.core.view.enums.CrudEvents;
import com.gitlab.pedrioko.core.view.enums.CrudMode;
import com.gitlab.pedrioko.core.view.enums.FormStates;
import com.gitlab.pedrioko.core.view.util.ApplicationContextUtils;
import com.gitlab.pedrioko.core.view.util.PropertiesUtil;
import com.gitlab.pedrioko.core.view.util.ZKUtil;
import com.gitlab.pedrioko.core.view.viewers.crud.controllers.CrudController;
import com.gitlab.pedrioko.core.view.viewers.crud.grid.AlphabetFilter;
import com.gitlab.pedrioko.core.view.viewers.crud.grid.CrudGrid;
import com.gitlab.pedrioko.core.view.viewers.crud.table.CrudTable;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.util.ArrayList;
import java.util.List;

import static com.gitlab.pedrioko.core.view.util.ApplicationContextUtils.getBean;

public class CrudView extends Tabpanel {

    private static final long serialVersionUID = 1L;
    private Class<?> klass;
    private @Getter
    CrudTable crudTable;
    private @Getter
    CrudGrid gridTable;
    private List<Button> listActions;
    private Div divbar;
    private @Getter
    @Setter
    Div actions;
    private List<Component> previusChilderns;
    private CrudViewBar toolbar;

    private @Getter
    @Setter
    Boolean reloadable;

    private @Getter
    @Setter
    CrudController crudController;
    private @Getter
    @Setter
    CrudMode crudviewmode;
    private @Getter
    East east;
    private CrudMenuContext popup;
    private North north;
    private CrudFilters crudFilters;
    private Borderlayout borderlayout;
    private int PAGE_SIZE = 16;
    private Paging paging;

    public CrudView() {
    }

    public CrudView(Class<?> klass) {
        super();
        crudviewmode = CrudMode.MAINCRUD;
        view(klass, getBean(PropertiesUtil.class).getFieldTable(klass));

    }

    public CrudView(Class<?> klass, CrudMode crudviewmode) {
        super();
        this.crudviewmode = crudviewmode;
        view(klass, getBean(PropertiesUtil.class).getFieldTable(klass));
    }

    public CrudView(Class<?> klass, Boolean useGrid) {
        super();
        init(klass, useGrid);
    }

    public void useAlphabetFilter() {
        if (gridTable != null) {
            North north = new North();
            north.appendChild(new AlphabetFilter(crudController, klass));
            gridTable.appendChild(north);
        }
    }

    protected void init(Class<?> klass, Boolean useGrid) {
        crudviewmode = CrudMode.MAINCRUD;
        this.klass = klass;
        if (useGrid) {
            gridTable = new CrudGrid(klass);
            createUI(gridTable);
            South south = new South();
            paging = new Paging();
            paging.setDetailed(true);
            if (ZKUtil.isMobile())
                paging.setMold("os");
            paging.setPageSize(PAGE_SIZE);
            paging.addEventListener("onPaging", (Event event) -> {
                PagingEvent pe = (PagingEvent) event;
                int pgno = pe.getActivePage();
                int ofs = pgno * PAGE_SIZE;
                crudController.setPage(ofs, PAGE_SIZE);
            });
            south.appendChild(paging);
            borderlayout.appendChild(south);
            crudController.addEventPostQuery(() -> gridTable.update());
            crudController.addEventPostQuery(() -> {
                int pc = paging.getActivePage();
                int count = (int) crudController.getCount();
                paging.setTotalSize(count);
                paging.setPageSize(PAGE_SIZE);
                int i = count / PAGE_SIZE;
                if (i <= pc)
                    crudController.setPage(i * PAGE_SIZE, PAGE_SIZE);

            });
            crudController.setPage(0, PAGE_SIZE);
            crudController.addEventOnEvent(CrudEvents.ON_ADD, () -> gridTable.update());
        } else {
            crudTable = new CrudTable(klass);
            createUI(crudTable);
            crudController.addEventPostQuery(() -> crudTable.update());
            crudController.addEventOnEvent(CrudEvents.ON_ADD, () -> crudTable.update());
        }
        crudController.doQuery();
        popup = new CrudMenuContext(klass, ApplicationContextUtils.getBeans(Action.class));
        appendChild(popup);
        gridTable.addEventOnEvent(CrudEvents.ON_RIGHT_CLICK, () -> {
            CrudActionEvent data = new CrudActionEvent(gridTable.getSelectedValue());
            data.setCrudViewParent(this);
            data.setFormstate(FormStates.READ);
            String position = ZKUtil.isMobile() ? "overlap_before" : "at_pointer";
            popup.open(gridTable.getCenter(), position, data);
        });
    }

    public void setPageSize(int PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
        int activePage = paging.getActivePage();
        crudController.setPage(activePage * PAGE_SIZE, PAGE_SIZE);
        crudController.doQuery();
    }

    private void createUI(Component table) {
        borderlayout = new Borderlayout();
        divbar = new Div();
        actions = new Div();
        toolbar = new CrudViewBar(klass, this, (CrudDisplayTable) table);
        divbar.appendChild(toolbar);
        borderlayout.setStyle("height:100%;");
        east = new East();
        north = new North();
        north.appendChild(divbar);
        borderlayout.appendChild(north);
        east.setTitle("Filters");
        east.setStyle(" overflow-y:auto !important; width:350px;");
        if (ZKUtil.isMobile()) {
            east.setCollapsible(false);
            east.setSplittable(false);
            east.setOpen(false);
            east.setSlide(true);
        } else {
            east.setSplittable(false);
            east.setCollapsible(true);
            east.setOpen(false);
            east.setVisible(true);
            east.setSlide(false);
        }
        borderlayout.appendChild(east);
        borderlayout.appendChild(new Center());
        appendChild(borderlayout);
        Center center = borderlayout.getCenter();
        center.appendChild(table);
        crudFilters = new CrudFilters(klass, this);
        east.appendChild(crudFilters);
        appendChild(actions);
        actions.setClass("col-md-12 col-lg-12 col-xs-12 col-sm-12");
        actions.setStyle("margin-top:10px;margin-bottom:10px;");
        setStyle("height:100%;");
        reloadable = crudviewmode != CrudMode.SUBCRUD;
        configController(klass, (CrudDisplayTable) table);
        east.addEventListener(Events.ON_VISIBILITY_CHANGE, e -> {
            if (!east.isVisible()) {
                clearParams();
                crudController.doQuery();
            }
        });

    }

    public CrudView(Class<?> klass, Object obj) {
        super();
        crudviewmode = CrudMode.MAINCRUD;
        this.klass = klass;
        crudTable = new CrudTable(klass, (List<Class<?>>) obj);
        crudController = new CrudController(klass, crudTable.getValue());
        createUI(crudTable);
    }

    private void view(Class<?> klass, List<String> fields) {
        this.klass = klass;
        listActions = new ArrayList<>();
        crudTable = new CrudTable(fields, klass);
        createUI(crudTable);
    }

    public Class<?> getTypeClass() {
        return klass;
    }

    private void configController(Class<?> klass, CrudDisplayTable table) {
        if (crudController == null) {
            crudController = new CrudController(klass, table.getValue());
            crudController.addEventPostQuery(() -> table.update());
            crudController.doQuery();
            crudController.addEventOnEvent(CrudEvents.ON_ADD, () -> table.update());
        }
    }

    public void previusState() {
        if (previusChilderns != null && !previusChilderns.isEmpty()) {
            getChildren().clear();
            previusChilderns.forEach(this::appendChild);
        }
        if (reloadable)
            update();
    }

    public List<String> getValueIds() {
        return crudController.getValueIds();
    }

    public void setValueIds(List<String> value) {
        crudController.setValueIds(value);
    }

    public void setContent(Component c) {
        previusChilderns = new ArrayList<>(getChildren());
        getChildren().clear();
        appendChild(c);
    }

    public <T> T getValue() {
        return (T) ((T) crudTable == null ? gridTable.getValue() : crudTable.getValue());
    }

    public void setValue(List<?> value) {
        crudController.setValue(value);
        if (gridTable != null) {
            paging.setTotalSize(value.size());
            paging.setPageSize(PAGE_SIZE);
            gridTable.clearSelection();
        }
    }

    public void setValue(ArrayList<?> value) {
        crudController.setValue(value);
    }

    public List<Button> getListActions() {
        return listActions;
    }

    public void setListActions(List<Button> listActions) {
        this.listActions = listActions;
    }

    public Div getToolbar() {
        return divbar;
    }

    public void setToolbar(Div toolbar) {
        divbar = toolbar;
    }

    public void setDisabled(boolean disable) {
        if (disable)
            divbar.setVisible(!disable);
    }

    public String getKlass() {
        return klass.getName();
    }

    public void setKlass(String klass) throws ClassNotFoundException {
        String[] split = klass.split(":");
        if (split.length > 1) {
            String s = split[0];
            Class<?> aClass = Class.forName(s);
            this.klass = aClass;
            boolean aBoolean = new Boolean(split[1]);
            init(aClass, aBoolean);
        } else {
            if (split.length > 3) {
                String s = split[0];
                Class<?> aClass = Class.forName(s);
                this.klass = aClass;
                crudviewmode = CrudMode.MAINCRUD;
                view(aClass, getBean(PropertiesUtil.class).getFieldTable(aClass));
            }
        }
    }


    public void enableCommonCrudActions(boolean disable) {
        toolbar.getCrudsActions().forEach(e -> e.setVisible(disable));
    }


    public void addValue(Object value) {
        crudController.addValue(value);
    }

    public void addAction(Action action, CrudActionEvent event) {
        addAction(action.getLabel(), action.getIcon(), "btn " + action.getClasses(), e -> {
            event.setValue(getSelectedValue());
            action.actionPerform(event);
        });
    }

    public void addAction(String labelaction, String icon, EventListener<? extends Event> event) {
        addAction(labelaction, icon, "btn btn-primary pull-left", event);
    }

    public void addAction(String labelaction, String icon, String classes, EventListener<? extends Event> event) {
        //      if (actions.getChildren().isEmpty())
//            setHeightTable("100%");
        Button btn = new Button();
        btn.setLabel(labelaction);
        btn.setIconSclass(icon);
        btn.setClass("btn btn-action " + classes);
        btn.addEventListener(Events.ON_CLICK, event);
        actions.appendChild(btn);

    }

    public void update() {
        if (crudTable == null) {
            gridTable.update();
        } else {
            crudTable.update();
        }
    }

    /**
     * @return
     * @see CrudTable#getSelectedValue()
     */
    private <T> T getSelectedValue() {
        return crudTable.getSelectedValue();
    }

    /**
     * @param actions
     * @see CrudViewBar#onlyEnable(java.util.List)
     */
    public void onlyEnable(List<String> actions) {
        if (actions.isEmpty())
            north.getChildren().clear();
        else
            toolbar.onlyEnable(actions);
    }


    public void setHeightTable(String height) {
        crudTable.setHeight(height);
    }

    public void addRootParams(String key, Object value) {
        crudController.putRoot(key, value);
    }

    public void getRootParams(String key) {
        crudController.getRoot(key);
    }


    void addParams(String key, Object value) {
        crudController.put(key, value);
    }

    public void getParams(String key) {
        crudController.get(key);
    }

    void clearParams() {
        crudController.clearParams();
    }

    public void enableAction(Class<? extends Action> action, boolean enable) {
        toolbar.enableAction(action, enable);
    }

    public void simpleUpdate(Object value) {
        if (crudTable == null) {
            gridTable.updateValue(value);
        } else {
            crudTable.update();
        }
    }
}
