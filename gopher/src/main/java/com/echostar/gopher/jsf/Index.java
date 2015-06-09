package com.echostar.gopher.jsf;

//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;

/**
Model for home page.
*/

//@ManagedBean
//@SessionScoped
public class Index {

    private String title = "Gopher";

	public Index () {}

    public String getTitle() {
        return title;
    }
}