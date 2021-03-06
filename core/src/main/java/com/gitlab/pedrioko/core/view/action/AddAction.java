package com.gitlab.pedrioko.core.view.action;

import com.gitlab.pedrioko.core.reflection.ReflectionZKUtil;
import com.gitlab.pedrioko.core.view.action.api.Action;
import com.gitlab.pedrioko.core.view.action.event.CrudActionEvent;
import com.gitlab.pedrioko.core.view.enums.FormStates;
import com.gitlab.pedrioko.core.view.enums.MessageType;
import com.gitlab.pedrioko.core.view.enums.SubCrudView;
import com.gitlab.pedrioko.core.view.forms.AddForm;
import com.gitlab.pedrioko.core.view.forms.CustomForm;
import com.gitlab.pedrioko.core.view.util.ApplicationContextUtils;
import com.gitlab.pedrioko.core.view.util.ArraysUtil;
import com.gitlab.pedrioko.core.view.util.ZKUtil;
import com.gitlab.pedrioko.core.view.viewers.crud.CrudView;
import com.gitlab.pedrioko.services.CrudService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Order(0)
public class AddAction implements Action {

    private static final String AGREGAR = "Agregar";

    @Override
    public String getIcon() {
        return "fa  fa-plus-square";
    }

    @Override
    public void actionPerform(CrudActionEvent event) {

        CrudView crudViewParent = (CrudView) event.getCrudViewParent();
        Class<?> typeClass = crudViewParent.getTypeClass();
        CustomForm form = new CustomForm(typeClass, new LinkedHashMap<>());
        form.addField(AGREGAR, Combobox.class);
        Combobox combobox = (Combobox) form.getComponentField(AGREGAR);
        List<? extends Object> all = ApplicationContextUtils.getBean(CrudService.class).getAll(typeClass);
        ArrayList<?> value = crudViewParent.getValue();
        ArraysUtil.removeDuplicates(typeClass, all, value);
        all.forEach(e -> {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(e.toString());
            comboitem.setValue(e);
            combobox.getItems().add(comboitem);
        });
        form.getRenglon(AGREGAR).setZclass("col-md-12 col-lg-12 col-xs-12 col-sm-12");
        form.addAction(ReflectionZKUtil.getLabel("agregar"), "fa fa-plus", e -> {
            crudViewParent.addValue(combobox.getSelectedItem().getValue());
            form.detach();
            ZKUtil.showMessage(ReflectionZKUtil.getLabel("userbasicform.guardar"), MessageType.SUCCESS);
        });
        form.addAction(ReflectionZKUtil.getLabel("cancelar"), "fa fa-ban", "btn btn-danger", e -> form.detach());
        AddForm addForm = new AddForm(typeClass.getSimpleName(), typeClass, Combobox.class, true, crudViewParent, e -> {
            crudViewParent.addValue(e.getData());
            e.getTarget().detach();
            ZKUtil.showMessage(ReflectionZKUtil.getLabel("userbasicform.guardar"), MessageType.SUCCESS);
        }, e -> form.detach());
        ZKUtil.showDialogWindow(addForm);

    }

    @Override
    public List<?> getAplicateClass() {
        return Arrays.asList(SubCrudView.class);
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public String getClasses() {
        return "btn-info";
    }

    @Override
    public FormStates getFormState() {
        return FormStates.READ;
    }

    @Override
    public Integer position() {
        return 0;
    }

    @Override
    public String getColor() {
        return "#01419f";
    }

    @Override
    public int getGroup() {
        return 2;
    }

    @Override
    public String getTooltipText() {
        return ReflectionZKUtil.getLabel("agregar");
    }

}
