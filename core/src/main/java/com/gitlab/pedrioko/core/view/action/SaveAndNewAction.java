package com.gitlab.pedrioko.core.view.action;

import com.gitlab.pedrioko.core.reflection.ReflectionJavaUtil;
import com.gitlab.pedrioko.core.reflection.ReflectionZKUtil;
import com.gitlab.pedrioko.core.view.action.api.Action;
import com.gitlab.pedrioko.core.view.action.event.CrudActionEvent;
import com.gitlab.pedrioko.core.view.enums.FormStates;
import com.gitlab.pedrioko.core.view.enums.MessageType;
import com.gitlab.pedrioko.core.view.forms.Form;
import com.gitlab.pedrioko.core.view.util.Validate;
import com.gitlab.pedrioko.core.view.util.ZKUtil;
import com.gitlab.pedrioko.core.view.viewers.crud.CrudView;
import com.gitlab.pedrioko.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SaveAndNewAction implements Action {

    @Autowired
    private CrudService crudservice;

    @Override
    public String getLabel() {
        return ReflectionZKUtil.getLabel("Save and new");
    }

    @Override
    public String getIcon() {
        return "z-icon-floppy-o";
    }

    @Override
    public void actionPerform(CrudActionEvent event) {
        CrudView crudViewParent = (CrudView) event.getCrudViewParent();
        Object val = event.getValue();
        ArrayList list = crudViewParent.getValue();
        if (Validate.noDuplicate(val) && event.getFormstate() != FormStates.UPDATE) {
            String noDuplicateValue = Validate.getNoDuplicateValue(val);
            ZKUtil.showMessage(noDuplicateValue + " - " + ReflectionZKUtil.getLabel("onlist"), MessageType.WARNING);
        } else {
            ReflectionJavaUtil.removeById(list, ReflectionJavaUtil.getIdValue(val));
            crudViewParent.addValue(crudservice.saveOrUpdate(val));
            boolean a = event.getFormstate() == FormStates.CREATE || event.getFormstate() == FormStates.UPDATE;
            if (a && crudViewParent.getReloadable()) {
                crudViewParent.getCrudController().doQuery();
            }
            crudViewParent.update();

            event.getSource().setValueForm(ReflectionJavaUtil.getNewInstace(val.getClass()));
            ZKUtil.showMessage(ReflectionZKUtil.getLabel("userbasicform.guardar"), MessageType.SUCCESS);

        }
    }

    @Override
    public List<?> getAplicateClass() {
        return Arrays.asList(Form.class);
    }

    @Override
    public String getClasses() {
        return "pull-left btn-primary";
    }

    @Override
    public FormStates getFormState() {
        return null;
    }

    @Override
    public Integer position() {
        return -1;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public int getGroup() {
        return 0;
    }

    @Override
    public String getTooltipText() {
        return null;
    }
}
