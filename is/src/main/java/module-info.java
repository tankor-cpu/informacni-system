module cz.vsb.is {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;

    requires spring.web;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.data.jpa;
    requires spring.orm;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.tx;
    requires jakarta.persistence;

    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.annotation;

    requires static lombok;
    requires com.fasterxml.jackson.databind;


    exports cz.vsb.is;
    exports cz.vsb.is.controller;
    exports cz.vsb.is.gui;
    exports cz.vsb.is.entity;

    opens cz.vsb.is to javafx.graphics;
    opens cz.vsb.is.controller to javafx.fxml;
    opens cz.vsb.is.gui to javafx.fxml;
    opens cz.vsb.is.entity to
            spring.core,
            spring.beans,
            spring.context,
            spring.data.jpa,
            spring.orm,
            org.hibernate.orm.core,
            com.fasterxml.jackson.databind;

}



