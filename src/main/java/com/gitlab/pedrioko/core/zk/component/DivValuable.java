package com.gitlab.pedrioko.core.zk.component;

import com.gitlab.pedrioko.core.lang.Point;
import lombok.Getter;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Div;

import java.io.IOException;

public class DivValuable extends Div {
    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        smartUpdate("value", value);
    }

    @Override
    protected void renderProperties(ContentRenderer renderer) throws IOException {
        super.renderProperties(renderer);
        render(renderer, "value", value);
    }

}
