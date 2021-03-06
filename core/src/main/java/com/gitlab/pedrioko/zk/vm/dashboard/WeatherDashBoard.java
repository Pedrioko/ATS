package com.gitlab.pedrioko.zk.vm.dashboard;

import com.gitlab.pedrioko.domain.enumdomain.TipoUsuario;
import com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

/**
 * The Class TestStatsDashBoard.
 */
//@Component
public class WeatherDashBoard implements DashBoardComponent {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #getContent()
     */
    @Override
    public Panel getContent() {
        Window window = (Window) Executions.createComponents("~./zul/content/dashboard/weather.zul", null, null);
        Panel panel = new Panel();
        Panelchildren panelchildren = new Panelchildren();
        panelchildren.appendChild(window);
        panel.appendChild(panelchildren);
        return panel;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #getForUser()
     */
    @Override
    public TipoUsuario[] getForUser() {
        return new TipoUsuario[]{TipoUsuario.ALL};
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #getIcon()
     */
    @Override
    public String getIcon() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #getLabel()
     */
    @Override
    public String getLabel() {
        return Labels.getLabel("clima");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #getPosicion()
     */
    @Override
    public int getPosicion() {
        return 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #hasViewMore()
     */
    @Override
    public boolean haveViewMore() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gitlab.pedrioko.zk.composer.interfaces.DashBoardComponent
     * #urlViewMore()
     */
    @Override
    public String urlViewMore() {
        return null;
    }

}
