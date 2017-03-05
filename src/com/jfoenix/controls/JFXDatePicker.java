/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfoenix.skins.JFXDatePickerSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * JFXDatePicker is the material design implementation of a date picker. 
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXDatePicker extends DatePicker {
	
	/**
	 * {@inheritDoc}
	 */
	public JFXDatePicker() {
		super();		
		initialize();
	}
	
	/**
	 * {@inheritDoc}
	 */
    public JFXDatePicker(LocalDate localDate) {
       super(localDate);
       initialize();
    }
	
	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
	}
    
	/**
	 * {@inheritDoc}
	 */
    @Override protected Skin<?> createDefaultSkin() {
        return new JFXDatePickerSkin(this);
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
	 * the parent node used when showing the data picker content as an overlay,
	 * intead of a popup
	 */
	private ObjectProperty<StackPane> dialogParent = new SimpleObjectProperty<>(null);
	public final ObjectProperty<StackPane> dialogParentProperty() {
		return this.dialogParent;
	}
	public final StackPane getDialogParent() {
		return this.dialogParentProperty().get();
	}
	public final void setDialogParent(final StackPane dialogParent) {
		this.dialogParentProperty().set(dialogParent);
	}

	/**
	 * property that holds the time value if showing the time picker
	 */
    private ObjectProperty<LocalTime> lastValidTime = new SimpleObjectProperty<>();
    
	public final ObjectProperty<LocalTime> timeProperty() {
		return this.lastValidTime;
	}
	public final java.time.LocalTime getTime() {
		return this.timeProperty().get();
	}
	public final void setTime(final java.time.LocalTime lastValidTime) {
		this.timeProperty().set(lastValidTime);
	}

	private boolean showTime = false;
    
	public boolean isShowTime() {
		return showTime;
	}

	/**
	 * indicates whether to pick time or date 
	 */
	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	/**
     * Initialize the style class to 'jfx-date-picker'.
     *
     * This is the selector class from which CSS can be used to style
     * this control.
     */
	private static final String DEFAULT_STYLE_CLASS = "jfx-date-picker";
    
	/**
	 * show the popup as an overlay using JFXDialog
	 * NOTE: to show it properly the scene root must be StackPane, or the user must set
	 * the dialog parent manually using the property {{@link #dialogParentProperty()}
	 */
	private StyleableBooleanProperty overLay = new SimpleStyleableBooleanProperty(StyleableProperties.OVERLAY, JFXDatePicker.this, "overLay", false);
	
	public final StyleableBooleanProperty overLayProperty() {
		return this.overLay;
	}
	public final boolean isOverLay() {
		return overLay == null ? false : this.overLayProperty().get();
	}
	public final void setOverLay(final boolean overLay) {
		this.overLayProperty().set(overLay);
	}
	
	/**
	 * the default color used in the data picker content
	 */
	private StyleableObjectProperty<Paint> defaultColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.DEFAULT_COLOR, JFXDatePicker.this, "defaultColor", Color.valueOf("#009688"));

	public Paint getDefaultColor(){
		return defaultColor == null ? Color.valueOf("#009688") : defaultColor.get();
	}
	public StyleableObjectProperty<Paint> defaultColorProperty(){		
		return this.defaultColor;
	}
	public void setDefaultColor(Paint color){
		this.defaultColor.set(color);
	}
    
	private static class StyleableProperties {
		private static final CssMetaData< JFXDatePicker, Paint> DEFAULT_COLOR =
				new CssMetaData< JFXDatePicker, Paint>("-fx-default-color",
						PaintConverter.getInstance(), Color.valueOf("#5A5A5A")) {
			@Override
			public boolean isSettable(JFXDatePicker control) {
				return control.defaultColor == null || !control.defaultColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXDatePicker control) {
				return control.defaultColorProperty();
			}
		};

		private static final CssMetaData< JFXDatePicker, Boolean> OVERLAY =
				new CssMetaData< JFXDatePicker, Boolean>("-fx-overlay",
						BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(JFXDatePicker control) {
				return control.overLay == null || !control.overLay.isBound();
			}
			@Override
			public StyleableBooleanProperty getStyleableProperty(JFXDatePicker control) {
				return control.overLayProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					DEFAULT_COLOR,
					OVERLAY);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		if(STYLEABLES == null){
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.CHILD_STYLEABLES;
	}

}
