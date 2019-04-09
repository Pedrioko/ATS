package com.gitlab.pedrioko.core.zk.viewmodel.crud.table;

import com.gitlab.pedrioko.core.lang.FileEntity;
import com.gitlab.pedrioko.core.view.action.api.Action;
import com.gitlab.pedrioko.core.view.action.event.CrudActionEvent;
import com.gitlab.pedrioko.core.view.reflection.ReflectionJavaUtil;
import com.gitlab.pedrioko.core.view.util.ApplicationContextUtils;
import com.gitlab.pedrioko.core.view.util.PropertiesUtil;
import com.gitlab.pedrioko.core.view.util.StringUtil;
import com.gitlab.pedrioko.core.view.viewers.crud.CrudView;
import com.gitlab.pedrioko.services.CrudService;
import com.gitlab.pedrioko.services.StorageService;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static com.gitlab.pedrioko.core.view.util.ApplicationContextUtils.getBean;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CrudTable {

    private Object selectValue;

    private List<Field> fieldList;
    private List<String> headers;
    private CrudView crudView;
    private Class<?> klass;
    private List<?> items;
    protected String uuidold = "";
    @WireVariable
    private CrudService crudService;
    private StorageService storageService;
    private String uuid;

    @Init
    private void init() {
        crudView = (CrudView) Executions.getCurrent().getArg().get("CrudView");
        klass = (Class<?>) Executions.getCurrent().getArg().get("klass-crud");
        headers = getBean(PropertiesUtil.class).getFieldTable(klass);
        items = (List<?>) Executions.getCurrent().getArg().get("crud-list-items");
        uuid = (String) Executions.getCurrent().getArg().get("CrudViewUUID");
        crudService = ApplicationContextUtils.getBean(CrudService.class);
        storageService = ApplicationContextUtils.getBean(StorageService.class);
        loadfields();
        EventQueues.lookup("action-crud-" + klass.getSimpleName(), EventQueues.SESSION, true).subscribe(event -> {
            if (event.getTarget() == crudView && event.getName().equalsIgnoreCase("action-crud-" + klass.getSimpleName() + "-" + uuid) /*&& !event.getName().equalsIgnoreCase(uuidold)*/) {
                Action action = (Action) event.getData();
                CrudActionEvent crudevent = new CrudActionEvent();
                crudevent.setCrudViewParent(crudView);
                crudevent.setType(klass);
                crudevent.setFormstate(action.getFormState());
                crudevent.setValue(selectValue);
                action.actionPerform(crudevent);
                uuidold = event.getName();
            }
        });
    }

    @NotifyChange({"items", "itemsRefresh"})
    @GlobalCommand
    public void refresh() {
        System.out.println("Update");
    }

    private void loadfields() {
        fieldList = ReflectionJavaUtil.getFields(klass);
        if (headers != null && !headers.isEmpty()) {
            List<Field> filter = fieldList.stream().filter(e -> headers.contains(e.getName()))
                    .collect(Collectors.toList());
            fieldList = filter;
        }
        headers = fieldList.stream().map(Field::getName).collect(Collectors.toList());
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }


    public Object getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(Object selectValue) {
        this.selectValue = selectValue;
    }

    public List<?> getItemsRefresh() {
        return items.stream().map(crudService::refresh).collect(Collectors.toList());
    }

    public List<?> getItems() {
        return items;
    }

    public void setItemsRefresh(List<?> items) {
        this.items = items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }

    public Object valueField(Object object, String fieldname) {
        return ReflectionJavaUtil.getValueFieldObject(fieldname, object);
    }

    @Command
    public void actionOnDoubleClick(@BindingParam("action") String action, @BindingParam("value")Object selectValue) {
        System.out.println("value");
        Action bean = (Action) getBean(StringUtil.getDescapitalize(action));
        CrudActionEvent event = new CrudActionEvent();
        event.setCrudViewParent(crudView);
        event.setValue(this.selectValue);
        event.setFormstate(bean.getFormState());
        event.setType(klass);
        bean.actionPerform(event);
    }

    public String loadFileEntityURL(Object value) {
        return storageService.getUrlFile((FileEntity) value);
    }
}