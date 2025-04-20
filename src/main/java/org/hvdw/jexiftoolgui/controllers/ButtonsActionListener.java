package org.hvdw.jexiftoolgui.controllers;

import org.hvdw.jexiftoolgui.ProgramTexts;
import org.hvdw.jexiftoolgui.Utils;
import org.hvdw.jexiftoolgui.editpane.EditGeotaggingdata;
import org.hvdw.jexiftoolgui.view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.slf4j.LoggerFactory.getLogger;

public class ButtonsActionListener implements ActionListener {
    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) getLogger(ButtonsActionListener.class);

    private ExifToolReferencePanel ETRP = new ExifToolReferencePanel();
    private SimpleWebView WV = new SimpleWebView();
    private Favorites Favs = new Favorites();
    private ExifToolCommands YourCmnds = new ExifToolCommands();
    private EditGeotaggingdata EGd = new EditGeotaggingdata();
    private MetadataUserCombinations MD = new MetadataUserCombinations();
    private StandardFileIO SFIO = new StandardFileIO();

    public JLabel OutputLabel;
    public JPanel rootPanel;
    public JTextField CommandsParameterstextField;
    public JTextField geotaggingImgFoldertextField;
    public JTextField geotaggingGPSLogtextField;
    public JTextField sqlQuerytextField;
    public JComboBox UserCombiscomboBox;
    public JTextField ExpImgFoldertextField;
    public JTextField ETCommandsFoldertextField;
    private String[] params;

    public ButtonsActionListener(JPanel rootPanel, JLabel OutputLabel, JTextField CommandsParameterstextField, JTextField geotaggingImgFoldertextField, JTextField geotaggingGPSLogtextField, JComboBox UserCombiscomboBox, JTextField ExpImgFoldertextField, JTextField ETCommandsFoldertextField) {

        this.rootPanel = rootPanel;
        this.OutputLabel = OutputLabel;
        this.CommandsParameterstextField = CommandsParameterstextField;
        this.geotaggingImgFoldertextField = geotaggingImgFoldertextField;
        this.geotaggingGPSLogtextField = geotaggingGPSLogtextField;
        this.sqlQuerytextField = sqlQuerytextField;
        this.UserCombiscomboBox = UserCombiscomboBox;
        this.ExpImgFoldertextField = ExpImgFoldertextField;
        this.ETCommandsFoldertextField = ETCommandsFoldertextField;
    }

    @Override
    public void actionPerformed(ActionEvent gav) { // gav = gui ActionEvent
        List<Integer> selectedIndicesList = new ArrayList<Integer>();
        OutputLabel.setText("");

        // This is not nice object oriented programming but gives a nice clear structured overview
        switch (gav.getActionCommand()) {
            case "bSI":
                logger.debug("button buttonShowImage pressed");
                Utils.displaySelectedImageInExternalViewer();
                break;
            case "CommandshB":
                logger.debug("button CommandshelpButton pressed");
                WV.HTMLView(ResourceBundle.getBundle("translations/program_help_texts").getString("exiftoolcommandstitle"), ResourceBundle.getBundle("translations/program_help_texts").getString("exiftoolcommands"), 700, 550);
                break;
            case "ExifhB":
            case "xmpHB":
                logger.debug("button Exifhelp or xmpHelp pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("exifandxmphelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("exifhelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "lCBBHB":
                logger.debug("button leftCheckBoxBarHelpButton pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("leftcheckboxbarhelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("leftcheckboxbartitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "geotHb":
                logger.debug("button geotagginghelpButton pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("geotagginghelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("geotagginghelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "gpsMcb":
                logger.debug("button gpsMapcoordinatesButton pressed");
                Utils.openBrowser("https://www.mapcoordinates.net/en");
                break;
            case "gpsHb":
                logger.debug("button gpsHelpbutton pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 600, ResourceBundle.getBundle("translations/program_help_texts").getString("gpshelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("gpshelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "CopyHb":
                logger.debug("button CopyHelpbutton pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("copymetadatatext")), ResourceBundle.getBundle("translations/program_help_texts").getString("copymetadatatitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "gpanoHb":
                logger.debug("button gpanoHelpbutton pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("gpanohelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("gpanohelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "lensHb":
                logger.debug("button lensHelpbutton pressed");
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("lenshelptext")), ResourceBundle.getBundle("translations/program_help_texts").getString("lenshelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "sPHb":
                logger.debug("button stringPlusHelpbutton pressed");
                break;
            case "CommandsclearPSFB":
                logger.debug("button CommandsclearParameterSFieldButton pressed");
                CommandsParameterstextField.setText("");
                break;
            case "ACommFavorb":
                logger.debug("button AddCommandFavoritebutton pressed");
                if (CommandsParameterstextField.getText().length()>0) {
                    Favs.showDialog(rootPanel, "AddFavorite", "Exiftool_Command", CommandsParameterstextField.getText());
                } else {
                    JOptionPane.showMessageDialog(rootPanel, ResourceBundle.getBundle("translations/program_strings").getString("msd.nocommandparams"), ResourceBundle.getBundle("translations/program_strings").getString("msd.nocommandparams"), JOptionPane.WARNING_MESSAGE);
                }
                break;
            case "LCommFavb":
                logger.debug("button LoadCommandFavoritebutton pressed");
                //YourCmnds.LoadCommandFavorite(rootPanel, CommandsParameterstextField);
                CommandsParameterstextField.setText(Favs.showDialog(rootPanel, "SelectFavorite", "Exiftool_Command", ""));
                break;
            case "geoIFb":
                logger.debug("button geotaggingImgFolderbutton pressed");
                //String ImgPath = EGd.getImagePath(rootPanel);
                String ImgPath = SFIO.getImagePath(rootPanel);
                if (!"".equals(ImgPath)) {
                    geotaggingImgFoldertextField.setText(ImgPath);
                }
                break;
            case "geoGPSLb":
                logger.debug("button geotaggingGPSLogbutton pressed");
                String TrackFile = EGd.gpsLogFile(rootPanel);
                if (!"".equals(TrackFile)) {
                    geotaggingGPSLogtextField.setText(TrackFile);
                }
                break;
            case "udcCNB":
                logger.debug("button udcCreateNewButton pressed");
                throw new IllegalArgumentException("Feature broken");
                // MD.showDialog(rootPanel);
                // MetadataUserCombinations MUC = new MetadataUserCombinations();
                // String[] views = MUC.loadCustomSets("fill_combo");
                // UserCombiscomboBox.setModel(new DefaultComboBoxModel(views));
                // break;
            case "udcHb":
                logger.debug("button udcHelpbutton pressed");
                //Utils.openBrowser(ProgramTexts.ProjectWebSite + "/manual/jexiftoolgui_usercombis.html");
                Utils.openBrowser(ProgramTexts.ProjectWebSite + "/manual/index.html#userdefinedmetadatacombinations");
                break;
            case "sidecarhelp":
                Utils.openBrowser(ProgramTexts.ProjectWebSite + "/manual/index.html#sidecar");
                break;
            case "expIFb":
                logger.debug("button ExpBrowseButton pressed");
                ImgPath = SFIO.getImagePath(rootPanel);
                if (!"".equals(ImgPath)) {
                    ExpImgFoldertextField.setText(ImgPath);
                }
                break;
            case "etCmdBtn":
                logger.debug("button ETCBrowseButton pressed");
                ImgPath = SFIO.getImagePath(rootPanel);
                if (!"".equals(ImgPath)) {
                    ETCommandsFoldertextField.setText(ImgPath);
                }
                break;
        }


    }
}
