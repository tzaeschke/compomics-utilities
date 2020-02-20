package com.compomics.software.dialogs;

import com.compomics.util.examples.BareBonesBrowserLaunch;
import com.compomics.util.gui.file_handling.FileChooserUtils;
import com.compomics.util.parameters.UtilitiesUserParameters;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * A dialog used to set up the connection to Reporter.
 *
 * @author Harald Barsnes
 */
public class ReporterSetupDialog extends javax.swing.JDialog {

    /**
     * The utilities preferences.
     */
    private UtilitiesUserParameters utilitiesUserParameters;
    /**
     * The selected folder.
     */
    private String lastSelectedFolder = "";
    /**
     * Set to true if the dialog was canceled.
     */
    private boolean dialogCanceled = true;

    /**
     * Creates a new ReporterSetupDialog.
     *
     * @param parent the parent dialog
     * @param modal if the dialog is to be modal or not
     * @throws FileNotFoundException if a FileNotFoundException occurs
     * @throws IOException if an IOException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     */
    public ReporterSetupDialog(JFrame parent, boolean modal) throws FileNotFoundException, IOException, ClassNotFoundException {
        super(parent, modal);

        initComponents();

        utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();

        // display the current reporter path
        if (utilitiesUserParameters != null) {
            reporterInstallationJTextField.setText(utilitiesUserParameters.getReporterPath());
            lastSelectedFolder = utilitiesUserParameters.getReporterPath();
        }

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        backgroundPanel = new javax.swing.JPanel();
        reporterInstallationPanel = new javax.swing.JPanel();
        reporterInstallationJTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        reporterJarFileHelpLabel = new javax.swing.JLabel();
        reporterDownloadPanel = new javax.swing.JPanel();
        reporterInfoLabel = new javax.swing.JLabel();
        reporterDownloadLinkLabel = new javax.swing.JLabel();
        reporterButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Reporter Settings");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        reporterInstallationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reporter Installation"));
        reporterInstallationPanel.setOpaque(false);

        reporterInstallationJTextField.setEditable(false);
        reporterInstallationJTextField.setToolTipText("The folder containing the Reporter jar file.");

        browseButton.setText("Browse");
        browseButton.setToolTipText("The folder containing the Reporter jar file.");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        reporterJarFileHelpLabel.setFont(reporterJarFileHelpLabel.getFont().deriveFont((reporterJarFileHelpLabel.getFont().getStyle() | java.awt.Font.ITALIC)));
        reporterJarFileHelpLabel.setText("Please locate the folder containing the Reporter jar file.");

        javax.swing.GroupLayout reporterInstallationPanelLayout = new javax.swing.GroupLayout(reporterInstallationPanel);
        reporterInstallationPanel.setLayout(reporterInstallationPanelLayout);
        reporterInstallationPanelLayout.setHorizontalGroup(
            reporterInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reporterInstallationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reporterInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reporterInstallationPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(reporterJarFileHelpLabel))
                    .addComponent(reporterInstallationJTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseButton)
                .addContainerGap())
        );
        reporterInstallationPanelLayout.setVerticalGroup(
            reporterInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reporterInstallationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reporterInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reporterInstallationJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reporterJarFileHelpLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        reporterDownloadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Download Reporter"));
        reporterDownloadPanel.setOpaque(false);

        reporterInfoLabel.setFont(reporterInfoLabel.getFont().deriveFont(reporterInfoLabel.getFont().getStyle() | java.awt.Font.BOLD));
        reporterInfoLabel.setText("Reporter - protein quantification based on reporter ions (iTRAQ and TMT)");

        reporterDownloadLinkLabel.setText("<html>Download here: <a href>https://compomics.github.io/projects/reporter.html</a></html>");
        reporterDownloadLinkLabel.setToolTipText("Go to https://compomics.github.io/projects/reporter.html");
        reporterDownloadLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reporterDownloadLinkLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                reporterDownloadLinkLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reporterDownloadLinkLabelMouseExited(evt);
            }
        });

        reporterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/reporter_logo.png"))); // NOI18N
        reporterButton.setToolTipText("Go to http://compomics.github.io/projects/reporter.html");
        reporterButton.setBorderPainted(false);
        reporterButton.setContentAreaFilled(false);
        reporterButton.setFocusPainted(false);
        reporterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reporterButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                reporterButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reporterButtonMouseExited(evt);
            }
        });

        javax.swing.GroupLayout reporterDownloadPanelLayout = new javax.swing.GroupLayout(reporterDownloadPanel);
        reporterDownloadPanel.setLayout(reporterDownloadPanelLayout);
        reporterDownloadPanelLayout.setHorizontalGroup(
            reporterDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reporterDownloadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reporterDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(reporterInfoLabel)
                    .addComponent(reporterDownloadLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 84, Short.MAX_VALUE)
                .addComponent(reporterButton)
                .addContainerGap())
        );
        reporterDownloadPanelLayout.setVerticalGroup(
            reporterDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reporterDownloadPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(reporterInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reporterDownloadLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(reporterButton)
        );

        okButton.setText("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reporterDownloadPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(reporterInstallationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        backgroundPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reporterInstallationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reporterDownloadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Close the dialog without saving.
     *
     * @param evt
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Open a file chooser were the user can select the Reporter jar file.
     *
     * @param evt
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        File selectedFile = FileChooserUtils.getUserSelectedFile(this, ".jar", "Reporter jar file (.jar)", "Select Reporter Jar File", lastSelectedFolder, null, true);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".jar")) {
                JOptionPane.showMessageDialog(this, "The selected file is not a jar file!", "Wrong File Selected", JOptionPane.WARNING_MESSAGE);
                okButton.setEnabled(false);
            } else if (!selectedFile.getName().contains("Reporter")) {
                JOptionPane.showMessageDialog(this, "The selected file is not a Reporter jar file!", "Wrong File Selected", JOptionPane.WARNING_MESSAGE);
                okButton.setEnabled(false);
            } else {
                // file assumed to be ok
                lastSelectedFolder = selectedFile.getPath();
                reporterInstallationJTextField.setText(lastSelectedFolder);
                okButton.setEnabled(true);
            }
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Save the Reporter mapping and close the dialog.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        // reload the user preferences as these may have been changed by other tools
        try {
            utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred when reading the user preferences.", "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        utilitiesUserParameters.setReporterPath(reporterInstallationJTextField.getText());
        try {
            UtilitiesUserParameters.saveUserParameters(utilitiesUserParameters);
            dialogCanceled = false;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the preferences.", "Error", JOptionPane.WARNING_MESSAGE);
        }
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Change the cursor to a hand cursor.
     *
     * @param evt
     */
    private void reporterDownloadLinkLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reporterDownloadLinkLabelMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_reporterDownloadLinkLabelMouseEntered

    /**
     * Change the cursor back to the default cursor.
     *
     * @param evt
     */
    private void reporterDownloadLinkLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reporterDownloadLinkLabelMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_reporterDownloadLinkLabelMouseExited

    /**
     * Opens the Reporter web page.
     *
     * @param evt
     */
    private void reporterDownloadLinkLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reporterDownloadLinkLabelMouseClicked
        openReporterWebPage();
    }//GEN-LAST:event_reporterDownloadLinkLabelMouseClicked

    /**
     * Change the cursor to a hand cursor.
     *
     * @param evt
     */
    private void reporterButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reporterButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_reporterButtonMouseEntered

    /**
     * Change the cursor back to the default cursor.
     *
     * @param evt
     */
    private void reporterButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reporterButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_reporterButtonMouseExited

    /**
     * Opens the Reporter web page.
     *
     * @param evt
     */
    private void reporterButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reporterButtonMouseClicked
        openReporterWebPage();
    }//GEN-LAST:event_reporterButtonMouseClicked

    /**
     * Close the dialog without saving.
     *
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton browseButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton okButton;
    private javax.swing.JButton reporterButton;
    private javax.swing.JLabel reporterDownloadLinkLabel;
    private javax.swing.JPanel reporterDownloadPanel;
    private javax.swing.JLabel reporterInfoLabel;
    private javax.swing.JTextField reporterInstallationJTextField;
    private javax.swing.JPanel reporterInstallationPanel;
    private javax.swing.JLabel reporterJarFileHelpLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * Opens the Reporter web page.
     */
    private void openReporterWebPage() {
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        BareBonesBrowserLaunch.openURL("https://compomics.github.io/projects/reporter.html");
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Returns true of the dialog was canceled by the user.
     *
     * @return the dialogCanceled
     */
    public boolean isDialogCanceled() {
        return dialogCanceled;
    }
}
