package com.compomics.software.dialogs;

import static com.compomics.software.autoupdater.DownloadLatestZipFromRepo.downloadLatestZipFromRepo;
import com.compomics.software.autoupdater.GUIFileDAO;
import com.compomics.util.examples.BareBonesBrowserLaunch;
import com.compomics.util.gui.file_handling.FileChooserUtils;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.compomics.util.parameters.UtilitiesUserParameters;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

/**
 * A dialog used to set up the connection to PeptideShaker.
 *
 * @author Harald Barsnes
 */
public class PeptideShakerSetupDialog extends javax.swing.JDialog {

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
     * The parent frame. Can be null.
     */
    private JFrame parentFrame = null;
    /**
     * The parent dialog. Can be null.
     */
    private JDialog parentDialog = null;
    /**
     * The progress dialog.
     */
    private ProgressDialogX progressDialog;

    /**
     * Creates a new PeptideShakerSetupDialog.
     *
     * @param parent the parent dialog
     * @param modal if the dialog is to be modal or not
     * @throws FileNotFoundException if a FileNotFoundException occurs
     * @throws IOException if an IOException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     */
    public PeptideShakerSetupDialog(
            JFrame parent, 
            boolean modal
    ) throws FileNotFoundException, IOException, ClassNotFoundException {
    
        super(parent, modal);
        initComponents();
        parentFrame = parent;
        setLocationRelativeTo(parent);
        setUpGUI();
    
    }

    /**
     * Creates a new PeptideShakerSetupDialog.
     *
     * @param parent the parent dialog
     * @param modal if the dialog is to be modal or not
     * @throws FileNotFoundException if a FileNotFoundException occurs
     * @throws IOException if an IOException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     */
    public PeptideShakerSetupDialog(
            JDialog parent, 
            boolean modal
    ) throws FileNotFoundException, IOException, ClassNotFoundException {
    
        super(parent, modal);
        initComponents();
        parentDialog = parent;
        setLocationRelativeTo(parent);
        setUpGUI();
        
    }

    /**
     * Set up the GUI.
     */
    private void setUpGUI() {
        
        utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();
        
        if (utilitiesUserParameters.getPeptideShakerPath() == null) {
            boolean downloaded = downloadPeptideShaker();
            if (downloaded) {
                dialogCanceled = false;
            } else {
                // display the current peptideshaker path
                if (utilitiesUserParameters != null) {
                    peptideShakernstallationJTextField.setText(utilitiesUserParameters.getPeptideShakerPath());
                    lastSelectedFolder = utilitiesUserParameters.getPeptideShakerPath();
                }

                setVisible(true);
            }
        } else {

            // display the current peptideshaker path
            if (utilitiesUserParameters != null) {
                peptideShakernstallationJTextField.setText(utilitiesUserParameters.getPeptideShakerPath());
                lastSelectedFolder = utilitiesUserParameters.getPeptideShakerPath();
            }

            setVisible(true);
        }
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
        peptideShakerInstallationPanel = new javax.swing.JPanel();
        peptideShakernstallationJTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        peptideShakerJarFileHelpLabel = new javax.swing.JLabel();
        peptideShakerDownloadPanel = new javax.swing.JPanel();
        peptideShakerInfoLabel = new javax.swing.JLabel();
        peptideShakerDownloadLinkLabel = new javax.swing.JLabel();
        peptideShakerButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("PeptideShaker Settings");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        peptideShakerInstallationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("PeptideShaker Installation"));
        peptideShakerInstallationPanel.setOpaque(false);

        peptideShakernstallationJTextField.setEditable(false);
        peptideShakernstallationJTextField.setToolTipText("The folder containing the PeptideShaker jar file.");

        browseButton.setText("Browse");
        browseButton.setToolTipText("The folder containing the PeptideShaker jar file.");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        peptideShakerJarFileHelpLabel.setFont(peptideShakerJarFileHelpLabel.getFont().deriveFont((peptideShakerJarFileHelpLabel.getFont().getStyle() | java.awt.Font.ITALIC)));
        peptideShakerJarFileHelpLabel.setText("Please locate the folder containing the PeptideShaker jar file.");

        javax.swing.GroupLayout peptideShakerInstallationPanelLayout = new javax.swing.GroupLayout(peptideShakerInstallationPanel);
        peptideShakerInstallationPanel.setLayout(peptideShakerInstallationPanelLayout);
        peptideShakerInstallationPanelLayout.setHorizontalGroup(
            peptideShakerInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideShakerInstallationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(peptideShakerInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(peptideShakerInstallationPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(peptideShakerJarFileHelpLabel))
                    .addComponent(peptideShakernstallationJTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseButton)
                .addContainerGap())
        );
        peptideShakerInstallationPanelLayout.setVerticalGroup(
            peptideShakerInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideShakerInstallationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(peptideShakerInstallationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(peptideShakernstallationJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(peptideShakerJarFileHelpLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        peptideShakerDownloadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Download PeptideShaker"));
        peptideShakerDownloadPanel.setOpaque(false);

        peptideShakerInfoLabel.setFont(peptideShakerInfoLabel.getFont().deriveFont(peptideShakerInfoLabel.getFont().getStyle() | java.awt.Font.BOLD));
        peptideShakerInfoLabel.setText("PeptideShaker - interpretation of proteomics identifications from multiple search engines");

        peptideShakerDownloadLinkLabel.setText("<html>Download here: <a href>https://compomics.github.io/projects/peptide-shaker.html</a></html>");
        peptideShakerDownloadLinkLabel.setToolTipText("Go to https://compomics.github.io/projects/peptide-shaker.html");
        peptideShakerDownloadLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                peptideShakerDownloadLinkLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                peptideShakerDownloadLinkLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                peptideShakerDownloadLinkLabelMouseExited(evt);
            }
        });

        peptideShakerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/peptide-shaker-medium-orange-shadow.png"))); // NOI18N
        peptideShakerButton.setToolTipText("Go to http://compomics.github.io/projects/peptide-shaker.html");
        peptideShakerButton.setBorderPainted(false);
        peptideShakerButton.setContentAreaFilled(false);
        peptideShakerButton.setFocusPainted(false);
        peptideShakerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                peptideShakerButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                peptideShakerButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                peptideShakerButtonMouseExited(evt);
            }
        });

        javax.swing.GroupLayout peptideShakerDownloadPanelLayout = new javax.swing.GroupLayout(peptideShakerDownloadPanel);
        peptideShakerDownloadPanel.setLayout(peptideShakerDownloadPanelLayout);
        peptideShakerDownloadPanelLayout.setHorizontalGroup(
            peptideShakerDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideShakerDownloadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(peptideShakerDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(peptideShakerDownloadPanelLayout.createSequentialGroup()
                        .addComponent(peptideShakerInfoLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideShakerDownloadPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(peptideShakerDownloadLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(peptideShakerButton)
                .addContainerGap())
        );
        peptideShakerDownloadPanelLayout.setVerticalGroup(
            peptideShakerDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideShakerDownloadPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(peptideShakerInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(peptideShakerDownloadLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(peptideShakerButton)
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
                    .addComponent(peptideShakerDownloadPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(peptideShakerInstallationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        backgroundPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(peptideShakerInstallationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(peptideShakerDownloadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
     * Open a file chooser were the user can select the PeptideShaker jar file.
     *
     * @param evt
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        File selectedFile = FileChooserUtils.getUserSelectedFile(
                this, 
                ".jar", 
                "PeptideShaker jar file (.jar)", 
                "Select PeptideShaker Jar File", 
                lastSelectedFolder, 
                null, 
                true
        );

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".jar")) {
                JOptionPane.showMessageDialog(this, "The selected file is not a jar file!", "Wrong File Selected", JOptionPane.WARNING_MESSAGE);
                okButton.setEnabled(false);
            } else if (!selectedFile.getName().contains("PeptideShaker")) {
                JOptionPane.showMessageDialog(this, "The selected file is not a PeptideShaker jar file!", "Wrong File Selected", JOptionPane.WARNING_MESSAGE);
                okButton.setEnabled(false);
            } else {

                // @TODO: check if this is the newest PeptideShaker version?
                // file assumed to be ok
                lastSelectedFolder = selectedFile.getPath();
                peptideShakernstallationJTextField.setText(lastSelectedFolder);
                okButton.setEnabled(true);
            }
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Save the PeptideShaker mapping and close the dialog.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        utilitiesUserParameters.setPeptideShakerPath(peptideShakernstallationJTextField.getText());
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
    private void peptideShakerDownloadLinkLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peptideShakerDownloadLinkLabelMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_peptideShakerDownloadLinkLabelMouseEntered

    /**
     * Change the cursor back to the default cursor.
     *
     * @param evt
     */
    private void peptideShakerDownloadLinkLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peptideShakerDownloadLinkLabelMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_peptideShakerDownloadLinkLabelMouseExited

    /**
     * Opens the PeptideShaker web page.
     *
     * @param evt
     */
    private void peptideShakerDownloadLinkLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peptideShakerDownloadLinkLabelMouseClicked

        // @TODO: should rather simply download the latest version?
        openPeptideShakerWebPage();
    }//GEN-LAST:event_peptideShakerDownloadLinkLabelMouseClicked

    /**
     * Change the cursor to a hand cursor.
     *
     * @param evt
     */
    private void peptideShakerButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peptideShakerButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_peptideShakerButtonMouseEntered

    /**
     * Change the cursor back to the default cursor.
     *
     * @param evt
     */
    private void peptideShakerButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peptideShakerButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_peptideShakerButtonMouseExited

    /**
     * Opens the PeptideShaker web page.
     *
     * @param evt
     */
    private void peptideShakerButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peptideShakerButtonMouseClicked
        openPeptideShakerWebPage();
    }//GEN-LAST:event_peptideShakerButtonMouseClicked

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
    private javax.swing.JButton peptideShakerButton;
    private javax.swing.JLabel peptideShakerDownloadLinkLabel;
    private javax.swing.JPanel peptideShakerDownloadPanel;
    private javax.swing.JLabel peptideShakerInfoLabel;
    private javax.swing.JPanel peptideShakerInstallationPanel;
    private javax.swing.JLabel peptideShakerJarFileHelpLabel;
    private javax.swing.JTextField peptideShakernstallationJTextField;
    // End of variables declaration//GEN-END:variables

    /**
     * Opens the PeptideShaker web page.
     */
    private void openPeptideShakerWebPage() {
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        BareBonesBrowserLaunch.openURL("https://compomics.github.io/projects/peptide-shaker.html");
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

    /**
     * Download PeptideShaker.
     *
     * @return true if not canceled
     */
    public boolean downloadPeptideShaker() {

        int option = JOptionPane.showConfirmDialog(this, "Cannot find PeptideShaker. "
                    + "Do you want to download it now? (Select \'No\' if already downloaded.)", "Download PeptideShaker?", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {

            String installPath = "user.home";

            if (utilitiesUserParameters.getPeptideShakerPath() != null) {
                if (new File(utilitiesUserParameters.getPeptideShakerPath()).getParentFile() != null
                        && new File(utilitiesUserParameters.getPeptideShakerPath()).getParentFile().getParentFile() != null) {
                    installPath = new File(utilitiesUserParameters.getPeptideShakerPath()).getParentFile().getParent();
                }
            }

            final File downloadFolder = FileChooserUtils.getUserSelectedFolder(this, "Select PeptideShaker Folder", installPath, "PeptideShaker Folder", "Select", false);

            if (downloadFolder != null) {

                if (parentFrame != null) {
                    progressDialog = new ProgressDialogX(parentFrame,
                            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker-orange.gif")),
                            true);
                } else if (parentDialog != null) {
                    progressDialog = new ProgressDialogX(parentDialog, (JFrame) parentDialog.getParent(),
                            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker-orange.gif")),
                            true);
                } else {
                    progressDialog = new ProgressDialogX(new JFrame(),
                            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker-orange.gif")),
                            true);
                }

                progressDialog.setPrimaryProgressCounterIndeterminate(true);
                progressDialog.setTitle("Downloading PeptideShaker. Please Wait...");

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            progressDialog.setVisible(true);
                        } catch (IndexOutOfBoundsException e) {
                            // ignore
                        }
                    }
                }, "ProgressDialog").start();

                Thread thread = new Thread("DownloadThread") {
                    @Override
                    public void run() {
                        try {
                            URL jarRepository = new URL("http", "genesis.ugent.be", new StringBuilder().append("/maven2/").toString());
                            downloadLatestZipFromRepo(downloadFolder, "PeptideShaker", "eu.isas.peptideshaker", "PeptideShaker", "peptide-shaker.ico",
                                    null, jarRepository, false, true, new GUIFileDAO(), progressDialog);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (XMLStreamException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (progressDialog.isRunCanceled()) {
                    progressDialog.setRunFinished();
                    return false;
                } else {
                    if (!progressDialog.isRunFinished()) {
                        progressDialog.setRunFinished();
                    }
                }

                return true;
            }
        }

        return false;
    }
}
