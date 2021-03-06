package com.gitlab.pedrioko.core.zk.viewmodel.crud.filters;

import com.gitlab.pedrioko.core.view.enums.CrudEvents;
import com.gitlab.pedrioko.core.view.viewers.crud.controllers.CrudController;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;

import java.util.Map;

public class Pagination {

    private CrudController crudController;
    private int activepage = 0;
    private long count;
    private int pagesize;

    @Init
    private void init() {
        Map<?, ?> arg = (Map<?, ?>) Executions.getCurrent().getArg();
        crudController = (CrudController) arg.get("crud-controller");
        count = crudController.getCount();
        pagesize = crudController.getLimit();
        crudController.addEventOnEvent(CrudEvents.ON_SET_PAGE_SIZE, this::refresh);
    }

    @Command
    public void paging() {
        int ofs = activepage * crudController.getLimit();
        crudController.setPage(ofs);
    }

    @NotifyChange("*")
    @GlobalCommand
    public void refresh() {
        count = crudController.getCount();
        pagesize = crudController.getLimit();
        if (activepage > crudController.getPage())
            activepage = 0;
    }

    public int getActivepage() {
        return activepage;
    }

    public void setActivepage(int activepage) {
        this.activepage = activepage;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }
}
