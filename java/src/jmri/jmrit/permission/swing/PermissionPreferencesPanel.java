package jmri.jmrit.permission.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import jmri.*;
import jmri.jmrit.permission.DefaultPermissionManager;
import jmri.swing.PreferencesPanel;

import org.openide.util.lookup.ServiceProvider;

/**
 * Preferences panel for Permission manager.
 *
 * @author Daniel Bergqvist Copyright 2024
 */
@ServiceProvider(service = PreferencesPanel.class)
public class PermissionPreferencesPanel extends JPanel implements PreferencesPanel {

    private final DefaultPermissionManager permissionManager;

    private boolean dirty = false;

    public PermissionPreferencesPanel() {
        PermissionManager mngr = InstanceManager.getDefault(PermissionManager.class);
        if (!(mngr instanceof DefaultPermissionManager)) {
            throw new RuntimeException("PermissionManager is not of type DefaultPermissionManager");
        }
        permissionManager = (DefaultPermissionManager)mngr;
        initGUI();
    }

    private void initGUI() {

        List<Role> roleList = new ArrayList<>(permissionManager.getRoles());
        roleList.sort((a,b) -> {
            if (a.getPriority() != b.getPriority()) {
                return Integer.compare(b.getPriority(), a.getPriority());
            }
            return a.getName().compareTo(b.getName());
        });

        JPanel outerPanel = new JPanel();

        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.PAGE_AXIS));

        JPanel settingsPanel = new JPanel();
        JCheckBox enablePermissionManager = new JCheckBox(Bundle.getMessage(
                "PermissionPreferencesPanel_EnablePermissionManager"));
        settingsPanel.add(enablePermissionManager);
        outerPanel.add(settingsPanel);

//        add(Box.createVerticalStrut(10));

        JPanel rolesPanel = new JPanel();
        rolesPanel.setLayout(new BoxLayout(rolesPanel, BoxLayout.PAGE_AXIS));

        JTabbedPane rolesTabbedPane = new JTabbedPane();

        for (Role role : roleList) {
            JPanel rolePanel = new JPanel();
            rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.PAGE_AXIS));

            rolesTabbedPane.addTab(role.getName(), new JScrollPane(rolePanel));

            JLabel roleLabel = new JLabel("<html><font size=\"+1\"><b>"+role.getName()+"</b></font></html>");
            roleLabel.setBorder(new EmptyBorder(4,4,0,4));
            rolePanel.add(roleLabel);

            for (PermissionOwner owner : permissionManager.getOwners()) {
                JPanel ownerPanel = new JPanel();
                ownerPanel.setLayout(new BoxLayout(ownerPanel, BoxLayout.PAGE_AXIS));

                JLabel ownerLabel = new JLabel("<html><font size=\"0.5\"><b>"+owner.getName()+"</b></font></html>");
                ownerLabel.setBorder(new EmptyBorder(15,4,4,4));
                rolePanel.add(ownerLabel);

                for (Permission permission : permissionManager.getPermissions(owner)) {
                    JCheckBox checkBox = new JCheckBox(permission.getName());
                    ownerPanel.add(checkBox);
                }
                rolePanel.add(ownerPanel);
            }

            rolePanel.add(Box.createVerticalStrut(10));
            JButton removeRoleButton = new JButton(Bundle.getMessage("PermissionPreferencesPanel_RemoveRole"));
            if (role.isSystemRole()) {
                removeRoleButton.setEnabled(false);
            }
            rolePanel.add(removeRoleButton);
        }

        rolesPanel.add(rolesTabbedPane);

        JButton addRoleButton = new JButton(Bundle.getMessage("PermissionPreferencesPanel_AddRole"));
        rolesPanel.add(addRoleButton);


        List<User> userList = new ArrayList<>(permissionManager.getUsers());
        userList.sort((a,b) -> {
            if (a.getPriority() != b.getPriority()) {
                return Integer.compare(b.getPriority(), a.getPriority());
            }
            return a.getUserName().compareTo(b.getUserName());
        });

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.PAGE_AXIS));

        JTabbedPane usersTabbedPane = new JTabbedPane();

        for (User user : userList) {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.PAGE_AXIS));

            usersTabbedPane.addTab(user.getUserName(), new JScrollPane(userPanel));

            JLabel usernameLabel = new JLabel("<html><font size=\"+1\"><b>"+user.getUserName()+"</b></font></html>");
            usernameLabel.setBorder(new EmptyBorder(4,4,4,4));
            userPanel.add(usernameLabel);
            userPanel.add(new JLabel("Name:"));
            userPanel.add(new JTextField(20));
            userPanel.add(new JLabel("Comment:"));
            userPanel.add(new JTextField(40));

            userPanel.add(Box.createVerticalStrut(10));

            int lastPriority = 0;
            for (Role role : roleList) {
                if (role.getPriority() == 0 && lastPriority != 0) {
                    userPanel.add(Box.createVerticalStrut(10));
                }
                JCheckBox checkBox = new JCheckBox(role.getName());
                userPanel.add(checkBox);
                lastPriority = role.getPriority();
            }

            userPanel.add(Box.createVerticalStrut(10));

            JButton changePasswordButton = new JButton(Bundle.getMessage("PermissionPreferencesPanel_ChangePassword"));
            userPanel.add(changePasswordButton);

            JButton removeUserButton = new JButton(Bundle.getMessage("PermissionPreferencesPanel_RemoveUser"));
            if (user.isSystemUser()) {
                removeUserButton.setEnabled(false);
            }
            userPanel.add(removeUserButton);
        }

        usersPanel.add(usersTabbedPane);

        JButton addUserButton = new JButton(Bundle.getMessage("PermissionPreferencesPanel_AddUser"));
        usersPanel.add(addUserButton);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(Bundle.getMessage("PermissionPreferencesPanel_Roles"),
                new JScrollPane(rolesPanel));
        tabbedPane.addTab(Bundle.getMessage("PermissionPreferencesPanel_Users"),
                new JScrollPane(usersPanel));

        JPanel outerTabbedPanel = new JPanel();
        outerTabbedPanel.add(tabbedPane);
        outerPanel.add(outerTabbedPanel);
        add(outerPanel);
    }

    @Override
    public String getPreferencesItem() {
        return "PREFERENCES"; // NOI18N
    }

    @Override
    public String getPreferencesItemText() {
        return Bundle.getMessage("MenuPermission"); // NOI18N
    }

    @Override
    public String getTabbedPreferencesTitle() {
        return getPreferencesItemText();
    }

    @Override
    public String getLabelKey() {
        return null;
    }

    @Override
    public JComponent getPreferencesComponent() {
        return this;
    }

    @Override
    public boolean isPersistant() {
        return false;
    }

    @Override
    public String getPreferencesTooltip() {
        return null;
    }

    @Override
    public void savePreferences() {
        permissionManager.storePermissionSettings();
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isRestartRequired() {
        return false;
    }

    @Override
    public boolean isPreferencesValid() {
        return true;
    }

//    @Override
//    public int getSortOrder() {
//        return PreferencesPanel.super.getSortOrder();
//    }

    @Override
    public BooleanSupplier getIsEnabled() {
        return () -> {
            return InstanceManager.getDefault(PermissionManager.class)
                    .checkPermission(PermissionsSystemAdmin.PERMISSION_EDIT_PERMISSIONS);
        };
    }
}
