package org.hvdw.jexiftoolgui.controllers;

import org.hvdw.jexiftoolgui.MyConstants;
import org.hvdw.jexiftoolgui.MyVariables;
import org.hvdw.jexiftoolgui.Utils;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class CommandRunner {
    public final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CommandRunner.class);

    /*
     * All exiftool commands go through this method
     */
    public static String runCommand(List<String> cmdparams) throws InterruptedException, IOException {

        StringBuilder res = new StringBuilder();
        List<String> newParams = new ArrayList<String>();
        List<String> imgList = new ArrayList<String>();
        List<String> postArgParams = new ArrayList<String>();
        byte[] myBytes = null;
        boolean bUTF8 = true;
        boolean versionCall = false;
        boolean winWhere = false;
        String[] supportedImages = MyConstants.SUPPORTED_IMAGES;
        List<String> supImgList = Arrays.asList(supportedImages);
        StringBuilder argsString = new StringBuilder();;

        // try with apache commons
        String platformCharset = Charset.defaultCharset().displayName();
        logger.debug("platformCharset: {}", platformCharset);
        logger.debug("cmdparams on entering runCommand: {}", cmdparams.toString().replaceAll(",", " "));
        //Always read/write exif data as utf8
        if (Utils.isOsFromMicrosoft()) {

            for (String subString : cmdparams) {
                if (subString.contains("preview")) {
                    bUTF8 = false;
                }
                if (subString.contains("jExifToolGUI")) {
                    bUTF8 = false;
                }
                if ((subString.toLowerCase()).contains("-ver")) {
                    bUTF8 = false;
                }
                if ((subString.toLowerCase()).contains("where")) {
                    bUTF8 = false;
                }
            }
            for (String subString : cmdparams) {
                // && !(subString.contains("exiftool.exe"))
                if (bUTF8) {
                    if ((subString.toLowerCase().contains("exiftool"))) {
                        //if ( (subString.toLowerCase().contains("exiftool")) && !(subString.contains("jExifToolGUI")) && !(subString.contains("preview")) ) {
                        newParams.add(subString);
                        newParams.add("-charset");
                        newParams.add("utf8");
                        newParams.add("-charset");
                        newParams.add("iptc=utf8");
                        newParams.add("-charset");
                        newParams.add("exif=utf8");
                        //newParams.add("-@");
                        //} else if ( (subString.toLowerCase().contains("jpg")) || (subString.toLowerCase().contains("tif")) || (subString.toLowerCase().contains("png")) )
                    } else if ((supImgList.stream().anyMatch(subString.toLowerCase()::endsWith)) && !(subString.toLowerCase().startsWith("-"))) {
                        // These are the images
                        imgList.add("\"" + subString + "\"");
                        logger.info("img subString {}", subString);
                    } else if (subString.contains("=") ) {
                        //These are strings that set tag values
                        argsString.append(subString + " \n");
                    } else {
                        postArgParams.add(subString);
                    }
                }
            }
            // Now write our argsString as Args file to tmp folder
            if (argsString.length() != 0) {
                // we need our argsfile @
                newParams.add("-@");
                File argsFile = new File(MyVariables.gettmpWorkFolder() + File.separator + "argfile.txt");

                // Use java 11 functionality for writing
                try (FileWriter fw = new FileWriter(argsFile, StandardCharsets.UTF_8);
                     BufferedWriter writer = new BufferedWriter(fw)) {
                    writer.append(argsString);
                    logger.info("argsString contains {}", argsString);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("error creating tmp argsfile on Windows {}", e.toString());
                }
                newParams.add(argsFile.toString());
            }
            //Now combine it all
            newParams.addAll(postArgParams);
            newParams.addAll(imgList);
        }
        logger.debug("total newParams {}\n", newParams.toString());
        // end try with apache commons

        ProcessBuilder builder = null;
        if ((Utils.isOsFromMicrosoft())) {
            if ( !(bUTF8) ) {
                builder = new ProcessBuilder(cmdparams);
            } else {
                builder = new ProcessBuilder(newParams);
                logger.info("newParams in processbuilder {}", newParams);
            }
        } else {
            builder = new ProcessBuilder(cmdparams);
            logger.debug("cmdparams in processbuilder {}", cmdparams);
        }
        logger.debug("Did ProcessBuilder builder = new ProcessBuilder(cmdparams);");
        try {
            builder.redirectErrorStream(true);
            Process process = builder.start();
            //Use a buffered reader to prevent hangs on Windows
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line).append(System.lineSeparator());
                logger.trace("tasklist: " + line);
            }
            process.waitFor();
        } catch (IOException e) {
            logger.error("IOException error {}", e.toString());
            res.append("IOException error")
                    .append(System.lineSeparator())
                    .append(e.getMessage());
        }
        return res.toString();
    }

    /*
     * This shows the output of exiftool after it has run
     */
    public static void outputAfterCommand(String output) {
        JTextArea textArea = new JTextArea(output);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Output from the given command", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void runCommandWithProgressBar(List<String> cmdparams, JProgressBar progressBar) { 
        runCommandWithProgressBar(cmdparams, progressBar, new Runnable() {

            @Override
            public void run() {
                
            }

        });
    }

    /*
     * This executes the commands via runCommand and shows/hides the progress bar
     * This one is special for the output windows and has 2 parameters
     */
    public static void runCommandWithProgressBar(List<String> cmdparams, JProgressBar progressBar, Runnable afterSuccess) {
        // Create executor thread to be able to update my gui when longer methods run
        Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = runCommand(cmdparams);
                    logger.debug("res is\n{}", res);
                    progressBar.setVisible(false);
                    outputAfterCommand(res);
                    try {
                        afterSuccess.run();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException | InterruptedException ex) {
                    logger.debug("Error executing command");
                }

            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setVisible(true);
            }
        });
    }

    /*
     * This executes the commands via runCommand and shows/hides the progress bar
     * This one has a 3rd parameter to enable/disable output. It should replace the above one (when I find the time)
     */
    public static void runCommandWithProgressBar(List<String> cmdparams, JProgressBar progressBar, String output) {
        // Create executor thread to be able to update my gui when longer methods run
        Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = runCommand(cmdparams);
                    logger.debug("res is\n {}", res);
                    progressBar.setVisible(false);
                    if ("delayed".equals(output.toLowerCase())) {
                        String tmp = MyVariables.getdelayedOutput();
                        MyVariables.setdelayedOutput(tmp + "\n\n" + res);
                        //outputAfterCommand(res);
                    }

                } catch (IOException | InterruptedException ex) {
                    logger.debug("Error executing command");
                }

            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setVisible(true);
            }
        });
    }


}
