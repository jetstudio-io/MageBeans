/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.adfab.magebeans.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author vanthiepnguyen
 */
public class CreateModuleProcess {
    final String PATH_TEMP;
    final String CONFIG_PATH;
    
    protected String codePool = "local";
    protected String projectDir = "";
    protected String moduleDir = "";
    protected String company = "Company";
    protected String module = "Module";
    protected String moduleName = "";
    protected String moduleNameLower = "";
    protected boolean hasBlock = false;
    protected boolean hasModel = false;
    protected boolean hasHelper = false;
    protected boolean hasSetup = false;
    
    protected Document dom;
    protected Element configElement;
    protected Element globalElement;
    
    public CreateModuleProcess() {
        this.PATH_TEMP = "%s/app/code/%s/%s/%s";
        this.CONFIG_PATH = "etc/config.xml";
    }

    public void setCompany(String company) {
        Pattern p = Pattern.compile("^[A-Z][a-z0-9]*$");
        Matcher m = p.matcher(company);
        if (m.matches()) {
            this.company = company;
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not good format", company));
        }
    }

    public void setModule(String module) {
        Pattern p = Pattern.compile("^[A-Z][a-z0-9]*$");
        Matcher m = p.matcher(module);
        if (m.matches()) {
            this.module = module;
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not good format", module));
        }
    }
    
    
    public void setCodePool(String codePool) {
        if ("local".equals(codePool) || "community".equals(codePool)) {
            this.codePool = codePool;
        } else {
            throw new IllegalArgumentException(String.format("Code pool '%s' does not allow", codePool));
        }
    }
    
    public void setProjectDir(String projectDir) throws FileNotFoundException {
        File tmp = new File(projectDir);
        if (!tmp.exists()) {
            throw new FileNotFoundException("Project directory not found");
        }
        this.projectDir = projectDir;
    }    

    public void setHasBlock(boolean hasBlock) {
        this.hasBlock = hasBlock;
    }

    public void setHasModel(boolean hasModel) {
        this.hasModel = hasModel;
    }

    public void setHasHelper(boolean hasHelper) {
        this.hasHelper = hasHelper;
    }

    public void setHasSetup(boolean hasSetup) {
        this.hasSetup = hasSetup;
    }
    
    
    public void process() throws FileNotFoundException {
        if ("".equals(this.projectDir)) {
            throw new FileNotFoundException("Project directory not found");
        }
        this.moduleName = this.company + "_" + this.module;
        this.moduleNameLower = this.moduleName.toLowerCase();
        this.moduleDir = String.format(this.PATH_TEMP, this.projectDir, this.codePool, this.company, this.module);
        this._createConfig();
        this._writeConfigFile();
    }
    
    
    protected void _createConfig() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.dom = documentBuilder.newDocument();
            this.configElement = this.dom.createElement("config");
            this.globalElement = this.dom.createElement("global");
            
            // Create config for module version
            Element moduleElement = this.dom.createElement(this.moduleName);
            Element versionElement = this.dom.createElement("version");
            versionElement.setNodeValue("0.0.1");
            moduleElement.appendChild(versionElement);
            this.configElement.appendChild(moduleElement);
            
            // Create config folder
            this._createFolders("etc");
            
            if (this.hasBlock) {
                this._createConfigNode("block");
                this._createFolders("Block");
            }
            
            if (this.hasModel) {
                this._createConfigNode("model");
                this._createFolders("Model");
            }
            
            if (this.hasHelper) {
                this._createConfigNode("helper");
                this._createFolders("Helper");
            }
            
            this.configElement.appendChild(this.globalElement);
            this.dom.appendChild(this.configElement);
        } catch (ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    protected void _writeConfigFile() {
        try {
            String configFile = this.moduleDir + "/" + this.CONFIG_PATH;
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            
            tr.transform(new DOMSource(dom), 
                                 new StreamResult(new FileOutputStream(configFile)));
        } catch (TransformerConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (TransformerException | FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    protected void _createConfigNode(String type) {
        String types = type + "s";
        Element classElement = this.dom.createElement("class");
        classElement.setNodeValue(this.moduleName + "_" + Utils.capitalize(type));
        
        Element moduleElement = this.dom.createElement(this.moduleNameLower);
        moduleElement.appendChild(classElement);
        
        Element typeElement = this.dom.createElement(types);
        typeElement.appendChild(moduleElement);
        
        this.globalElement.appendChild(typeElement);
    }
    /**
     * Create module folders
     */
    protected void _createFolders(String type) {
        String dir;
        dir = this.moduleDir + "/" + type;
        File tmp = new File(dir);
        tmp.mkdirs();
    }
}
