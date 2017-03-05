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

import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.CachedTransition;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * JFXPopup is the material design implementation of a popup.
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
@DefaultProperty(value="content")
public class JFXPopup extends AnchorPane {

	public static enum PopupHPosition{ RIGHT, LEFT };
	public static enum PopupVPosition{ TOP, BOTTOM };

	private AnchorPane contentHolder;

	private Scale scaleTransform = new Scale(0,0,0,0);
	private double offsetX = -1;
	private double offsetY = -1;

	private Pane popupContainer;
	private Region content;
	private Transition animation;
	private Node source;

	/**
	 * creates empty popup
	 */
	public JFXPopup(){
		this(null,null);
	}

	/**
	 * creates popup with a specified container and content 
	 * @param popupContainer the container where the popup will be added (e.g the root of the scene)
	 * @param content the node that will be shown in the popup
	 */
	public JFXPopup(Pane popupContainer, Region content) {
		initialize();
		setContent(content);
		setPopupContainer(popupContainer);
	}


	/***************************************************************************
	 *                                                                         *
	 * Setters / Getters                                                       *
	 *                                                                         *
	 **************************************************************************/

	public Pane getPopupContainer() {
		return popupContainer;
	}

	public void setPopupContainer(Pane popupContainer) {
		if(popupContainer!=null){
			this.popupContainer = popupContainer;
			// close the popup if clicked on the overlay pane
			this.setOnMouseClicked((e)->{ if(e.isStillSincePress())close(); });
			animation = new PopupTransition();
		}
	}

	public Region getContent() {
		return content;
	}

	public void setContent(Region content) {
		if(content!=null){
			this.content = content;
			contentHolder = new AnchorPane();
			contentHolder.getChildren().add(this.content);
			// bind the content holder size to its content
			contentHolder.prefWidthProperty().bind(this.content.prefWidthProperty());
			contentHolder.prefHeightProperty().bind(this.content.prefHeightProperty());
			contentHolder.getStyleClass().add("jfx-popup-container");
			contentHolder.getTransforms().add(scaleTransform);			
			JFXDepthManager.setDepth(contentHolder, 4);
			// to allow closing he popup when clicking on the shadowed area
			contentHolder.setPickOnBounds(false);
			
			// ensure stackpane is never resized beyond it's preferred size
			this.getChildren().add(contentHolder);
			this.getStyleClass().add("jfx-popup-overlay-pane");
			this.setVisible(false);			
			// prevent propagating the events to overlay pane
			contentHolder.addEventHandler(MouseEvent.ANY, (e)->e.consume());
		}
	}

	public Node getSource() {
		return source;
	}

	/**
	 * set the node that will trigger the popup upon an action
	 * @param source
	 */
	public void setSource(Node source) {
		this.source = source;
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * show the popup in the specified container with the specified position
	 * 
	 * @param vAlign can be TOP/BOTTOM
	 * @param hAlign can be LEFT/RIGHT
	 * @param popupContainer parent the show the popup inside (e.g the root of the scene)
	 */
	public void show(PopupVPosition vAlign, PopupHPosition hAlign, Pane popupContainer){
		this.setPopupContainer(popupContainer);
		this.show(vAlign, hAlign);
	}

	/**
	 * show the popup according to the specified position
	 * 
	 * @param vAlign can be TOP/BOTTOM
	 * @param hAlign can be LEFT/RIGHT
	 */
	public void show(PopupVPosition vAlign, PopupHPosition hAlign ){
		this.show(vAlign, hAlign, 0, 0);
	}

	/**
	 * show the popup according to the specified position with a certain offset
	 * 
	 * @param vAlign can be TOP/BOTTOM
	 * @param hAlign can be LEFT/RIGHT
	 * @param initOffsetX on the x axis
	 * @param initOffsetY on the y axis
	 */
	public void show(PopupVPosition vAlign, PopupHPosition hAlign, double initOffsetX, double initOffsetY ){

		offsetX = 0;
		offsetY = 0;
		// compute the position of the popup
		Node tempSource = this.source;
		Bounds bound = tempSource.localToParent(tempSource.getBoundsInLocal());
		offsetX = bound.getMinX() + initOffsetX;
		offsetY = bound.getMinY() + initOffsetY;
		
		// set the scene root as popup container if it's not set by the user
		if(popupContainer == null) this.setPopupContainer((Pane) this.source.getScene().getRoot());
		// add the popup to be rendered
		if(!popupContainer.getChildren().contains(this)) this.popupContainer.getChildren().add(this);
		
		while(!tempSource.getParent().equals(popupContainer)){
			tempSource = tempSource.getParent();
			bound = tempSource.localToParent(tempSource.getBoundsInLocal());
			// handle scroll pane case
			if(tempSource.getClass().getName().contains("ScrollPaneSkin")){
				offsetX += bound.getMinX();
				offsetY += bound.getMinY();
			}if(tempSource instanceof JFXTabPane){
				offsetX -= bound.getWidth() * ((JFXTabPane)tempSource).getSelectionModel().getSelectedIndex();				
			}else{				
				if(bound.getMinX() > 0) offsetX += bound.getMinX();
				if(bound.getMinY() > 0) offsetY += bound.getMinY();	
			}
		}
	
		// postion the popup according to its animation
		if(hAlign.equals(PopupHPosition.RIGHT)){
			scaleTransform.pivotXProperty().bind(content.widthProperty());
			contentHolder.translateXProperty().bind(Bindings.createDoubleBinding(()-> -content.getWidth() + source.getBoundsInLocal().getWidth()  + offsetX , content.widthProperty(),source.boundsInLocalProperty()));
		}else {
			scaleTransform.pivotXProperty().unbind();
			contentHolder.translateXProperty().unbind();
			scaleTransform.setPivotX(0);
			contentHolder.setTranslateX(offsetX);
		}

		if(vAlign.equals(PopupVPosition.BOTTOM)){
			scaleTransform.pivotYProperty().bind(content.heightProperty());
			contentHolder.translateYProperty().bind(Bindings.createDoubleBinding(()-> -content.getHeight() + source.getBoundsInLocal().getHeight()  + offsetY , content.heightProperty(),source.boundsInLocalProperty()));
		}else {
			scaleTransform.pivotYProperty().unbind();
			contentHolder.translateYProperty().unbind();
			scaleTransform.setPivotY(0);
			contentHolder.setTranslateY(offsetY);
		}

		animation.setRate(1);
		animation.setOnFinished((e)->{});
		animation.play();
	}


	/**
	 * close the popup, by default the popup will close when clicking outside the popup
	 * content and inside its container
	 */
	public void close(){
		animation.setRate(-1);
		animation.play();
		animation.setOnFinished((e)->{
			resetProperties();
		});
	}

	/***************************************************************************
	 *                                                                         *
	 * Animations                                                             *
	 *                                                                         *
	 **************************************************************************/


	private void resetProperties(){
		this.popupContainer.getChildren().remove(this);
		this.setVisible(false);
		scaleTransform.setX(0);
		scaleTransform.setY(0);
	}


	private class PopupTransition extends CachedTransition {

		public PopupTransition() {
			super(JFXPopup.this, new  Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(JFXPopup.this.visibleProperty(), false ,Interpolator.EASE_BOTH),
							new KeyValue(content.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(10),
									new KeyValue(JFXPopup.this.visibleProperty(), true ,Interpolator.EASE_BOTH),
									new KeyValue(JFXPopup.this.opacityProperty(), 0 ,Interpolator.EASE_BOTH),
									new KeyValue(scaleTransform.xProperty(), 0,Interpolator.EASE_BOTH),
									new KeyValue(scaleTransform.yProperty(), 0,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(700),
											new KeyValue(content.opacityProperty(), 0 ,Interpolator.EASE_BOTH),
											new KeyValue(scaleTransform.xProperty(), 1,Interpolator.EASE_BOTH)
											),		
											new KeyFrame(Duration.millis(1000),
													new KeyValue(content.opacityProperty(), 1 ,Interpolator.EASE_BOTH),
													new KeyValue(JFXPopup.this.opacityProperty(), 1 ,Interpolator.EASE_BOTH),
													new KeyValue(scaleTransform.yProperty(), 1  ,Interpolator.EASE_BOTH)

													)
					)
					);
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}

	}

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	 /**
     * Initialize the style class to 'jfx-popup'.
     *
     * This is the selector class from which CSS can be used to style
     * this control.
     */
	private static final String DEFAULT_STYLE_CLASS = "jfx-popup";

	private void initialize() {
		this.setVisible(false);
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}


}
