package org.vaadin.pontus.vaadinrx2.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

@Theme("demo")
@Title("Reactive Vaadin Add-on Demo")
@SuppressWarnings("serial")
@Push(transport = Transport.WEBSOCKET_XHR)
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(new DashBoard());

    }


}
