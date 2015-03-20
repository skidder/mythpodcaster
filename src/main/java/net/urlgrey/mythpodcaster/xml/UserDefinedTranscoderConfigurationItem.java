/*
 * UserDefinedTranscoderConfigurationItem.java
 * 
 * Created: Jun 2, 2010
 * 
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.xml;

import java.util.Arrays;
import java.util.List;

/**
 * @author scottkidder
 * 
 */
public class UserDefinedTranscoderConfigurationItem extends GenericTranscoderConfigurationItem {

  private String encoderArguments;
  private String inputVariableName;
  private String outputVariableName;
  private List<String> parsedEncoderArguments;

  public String getInputVariableName() {
    return inputVariableName;
  }

  public void setInputVariableName(String inputVariableName) {
    this.inputVariableName = inputVariableName;
  }

  public String getOutputVariableName() {
    return outputVariableName;
  }

  public void setOutputVariableName(String outputVariableName) {
    this.outputVariableName = outputVariableName;
  }

  public String getEncoderArguments() {
    return encoderArguments;
  }

  public void setEncoderArguments(String encoderArguments) {
    this.encoderArguments = encoderArguments;
    this.parsedEncoderArguments = Arrays.asList(encoderArguments.split(" "));
  }

  public List<String> getParsedEncoderArguments() {
    return parsedEncoderArguments;
  }

}
