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
package com.jfoenix.validation.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * An abstract class that defines the basic validation functionalities for a certain control.
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public abstract class ValidatorBase extends Parent {
	public static final String DEFAULT_ERROR_STYLE_CLASS = "error";

	public ValidatorBase(){
		parentProperty().addListener((o,oldVal,newVal)->parentChanged());
	}

	/***************************************************************************
	 *                                                                         *
	 * Methods                                                                 *
	 *                                                                         *
	 **************************************************************************/

	private void parentChanged() {
		updateSrcControl();
	}

	private void updateSrcControl(){
		Parent parent = getParent();
		if (parent != null) {
			Node control = parent.lookup(getSrc());
			srcControl.set(control);
		}
	}

	/**
	 * will validate the source control
	 */
	public void validate(){
		eval();
		onEval();
	}
	
	/**
	 * will evalutate the validation condition once calling validate method
	 */
	protected abstract void eval();

	/**
	 *	this method will update the source control after evaluating the validation condition 
	 */
	protected void onEval(){
		Node control = getSrcControl();
		if (hasErrors.get()) {
			/*
			 * TODO
			 * NOTE: NEED TO FIX adding error style class to text area 
			 * is causing the caret to disapper
			 */
			if (!control.getStyleClass().contains(errorStyleClass.get()))
				control.getStyleClass().add(errorStyleClass.get());
		} else{
			control.getStyleClass().remove(errorStyleClass.get());
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/***** srcControl *****/
	protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

	public void setSrcControl(Node srcControl){
		this.srcControl.set(srcControl);
	}

	public Node getSrcControl(){
		return this.srcControl.get();
	}

	public ObjectProperty<Node> srcControlProperty(){
		return this.srcControl;
	}


	/***** src *****/
	protected SimpleStringProperty src = new SimpleStringProperty(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};

	public void setSrc(String src){
		this.src.set(src);
	}

	public String getSrc(){
		return this.src.get();
	}

	public StringProperty srcProperty(){
		return this.src;
	}


	/***** hasErrors *****/
	protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

	public boolean getHasErrors(){
		return hasErrors.get();
	}

	public ReadOnlyBooleanProperty hasErrorsProperty(){
		return hasErrors.getReadOnlyProperty();
	}

	/***** Message *****/
	protected SimpleStringProperty message = new SimpleStringProperty(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};

	public void setMessage(String msg){
		this.message.set(msg);
	}

	public String getMessage(){
		return this.message.get();
	}

	public StringProperty messageProperty(){
		return this.message;
	}

	/***** Awsome Icon *****/
	protected SimpleObjectProperty<Node> icon = new SimpleObjectProperty<Node>(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};
	public void setIcon(Node icon){
		icon.getStyleClass().add("errorIcon");
		this.icon.set(icon);
	}
	public Node getIcon(){
		return this.icon.get();
	}
	public SimpleObjectProperty<Node> iconProperty(){
		return this.icon;
	}
	
	
	/***** error style class *****/
	protected SimpleStringProperty errorStyleClass = new SimpleStringProperty(DEFAULT_ERROR_STYLE_CLASS);

	public void setErrorStyleClass(String styleClass){
		this.errorStyleClass.set(styleClass);
	}

	public String getErrorStyleClass(){
		return this.errorStyleClass.get();
	}

	public StringProperty errorStyleClassProperty(){
		return this.errorStyleClass;
	}

}
