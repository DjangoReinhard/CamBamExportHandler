package de.schwarzrot.cambam;
/* 
 * **************************************************************************
 * 
 *  file:       CamBamExportHandler.java
 *  project:    GUI for linuxcnc
 *  subproject: exporthandler
 *  purpose:    exports tooldefinitions in the format used by cambam
 *  created:    3.12.2019 by Django Reinhard
 *  copyright:  all rights reserved
 * 
 *  This program is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation, either version 2 of the License, or 
 *  (at your option) any later version. 
 *   
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details. 
 *   
 *  You should have received a copy of the GNU General Public License 
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * **************************************************************************
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.schwarzrot.linuxcnc.data.CategoryInfo;
import de.schwarzrot.linuxcnc.data.LibInfo;
import de.schwarzrot.linuxcnc.data.ToolInfo;
import de.schwarzrot.linuxcnc.export.IExportHandler;
import de.schwarzrot.util.PropertyAccessor;


public class CamBamExportHandler implements IExportHandler {
   public static class Tuple {
      public Tuple(String external, String internal) {
         this.internal = internal;
         this.external = external;
      }

      public String internal;
      public String external;
   }


   @Override
   public void closeCategory(CategoryInfo catInfo) throws Exception {
      // do nothing as CamBam has no tool categories
   }


   @Override
   public void closeLibrary(LibInfo libInfo) throws Exception {
      xmlWriter.writeEndElement();
      xmlWriter.writeCharacters(NL);
      xmlWriter.writeEndDocument();
      xmlWriter.close();
   }


   @Override
   public void closeTool(ToolInfo toolInfo) throws Exception {
      // do nothing as tools don't have children
   }


   @Override
   public void openCategory(CategoryInfo catInfo) throws Exception {
      // do nothing as CamBam has not tool categories
   }


   @Override
   public void openLibrary(LibInfo libInfo, String fileName) throws Exception {
      xmlWriter = createXmlWriter(fileName);
      xmlWriter.writeStartDocument();
      xmlWriter.writeCharacters(NL);

      xmlWriter.writeStartElement(TLString);
      xmlWriter.writeAttribute(VString, libInfo.getVersion());
      xmlWriter.writeAttribute(TNFString, libInfo.getToolNameTemplate());
      xmlWriter.writeAttribute(NFString, libInfo.getNumberFormat());
   }


   @Override
   public void openTool(ToolInfo toolInfo) throws Exception {
      xmlWriter.writeCharacters(NL);
      xmlWriter.writeStartElement(TDString);
      xmlWriter.writeAttribute(NString, toolInfo.getToolName());
      xmlWriter.writeCharacters(NL);

      for (String pn : exportNames.keySet()) {
         String en    = exportNames.get(pn).external;
         Object value = pa.getProperty(toolInfo, pn);

         xmlWriter.writeStartElement(en);
         xmlWriter.writeCharacters(value.toString());
         xmlWriter.writeEndElement();
         xmlWriter.writeCharacters(NL);
      }
   }


   protected XMLStreamWriter createXmlWriter(String fileName) {
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      FileWriter       fw  = null;
      XMLStreamWriter  rv  = null;

      try {
         if (!fileName.endsWith(".xml")) fileName = fileName + ".xml";
         fw = new FileWriter(new File(fileName));
         rv = xof.createXMLStreamWriter(fw);

         return rv;
      }
      catch (IOException | XMLStreamException e) {
         e.printStackTrace();
      }
      return null;
   }

   private XMLStreamWriter           xmlWriter;
   private static final String       NL        = "\n";
   private static final String       TLString  = "ToolLibrary";
   private static final String       TDString  = "ToolDefinition";
   private static final String       TNFString = "ToolNameFormat";
   private static final String       NFString  = "NumberFormat";
   private static final String       NString   = "Name";
   private static final String       VString   = "Version";
   private static PropertyAccessor   pa        =
                                        new PropertyAccessor(ToolInfo.class);
   private static Map<String, Tuple> exportNames;
   static {
      exportNames = new HashMap<String, Tuple>();
      Tuple t = new Tuple("Index", "toolNumber");

      exportNames.put(t.internal, t);

      t = new Tuple("Name", "toolName");
      exportNames.put(t.internal, t);

      t = new Tuple("Diameter", "fluteDiameter");
      exportNames.put(t.internal, t);

      t = new Tuple("ToolProfile", "profile");
      exportNames.put(t.internal, t);

      t = new Tuple("Flutes", "flutes");
      exportNames.put(t.internal, t);

      t = new Tuple("FluteLength", "fluteLength");
      exportNames.put(t.internal, t);

      t = new Tuple("Length", "freeLength");
      exportNames.put(t.internal, t);

      t = new Tuple("ShankDiameter", "shankDiameter");
      exportNames.put(t.internal, t);

      t = new Tuple("HelixAngle", "helixAngle");
      exportNames.put(t.internal, t);

      t = new Tuple("VeeAngle", "cuttingAngle");
      exportNames.put(t.internal, t);

      t = new Tuple("MaxRampAngle", "maxRampAngle");
      exportNames.put(t.internal, t);

      t = new Tuple("ToothLoad", "toothLoad");
      exportNames.put(t.internal, t);

      t = new Tuple("AxialDepthOfCut", "cuttingLength");
      exportNames.put(t.internal, t);

      t = new Tuple("RadialDepthOfCut", "cuttingRadius");
      exportNames.put(t.internal, t);
   }
}
