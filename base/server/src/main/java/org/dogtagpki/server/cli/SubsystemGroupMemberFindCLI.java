//
// Copyright Red Hat, Inc.
//
// SPDX-License-Identifier: GPL-2.0-or-later
//
package org.dogtagpki.server.cli;

import java.util.Enumeration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.dogtagpki.cli.CLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netscape.certsrv.group.GroupMemberCollection;
import com.netscape.certsrv.group.GroupMemberData;
import com.netscape.certsrv.group.GroupNotFoundException;
import com.netscape.cmscore.apps.EngineConfig;
import com.netscape.cmscore.ldapconn.LDAPConfig;
import com.netscape.cmscore.ldapconn.LDAPConnectionConfig;
import com.netscape.cmscore.ldapconn.LdapAuthInfo;
import com.netscape.cmscore.ldapconn.LdapConnInfo;
import com.netscape.cmscore.ldapconn.PKISocketConfig;
import com.netscape.cmscore.ldapconn.PKISocketFactory;
import com.netscape.cmscore.usrgrp.Group;
import com.netscape.cmscore.usrgrp.UGSubsystem;
import com.netscape.cmscore.usrgrp.UGSubsystemConfig;
import com.netscape.cmsutil.password.IPasswordStore;
import com.netscape.cmsutil.password.PasswordStoreConfig;

/**
 * @author Endi S. Dewata
 */
public class SubsystemGroupMemberFindCLI extends SubsystemCLI {

    public static Logger logger = LoggerFactory.getLogger(SubsystemGroupMemberFindCLI.class);

    public SubsystemGroupMemberFindCLI(CLI parent) {
        super("find", "Find " + parent.getParent().getParent().getName().toUpperCase() + " group members", parent);
    }

    @Override
    public void createOptions() {
        Option option = new Option(null, "output-format", true, "Output format: text (default), json.");
        option.setArgName("format");
        options.addOption(option);
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {

        String[] cmdArgs = cmd.getArgs();

        if (cmdArgs.length < 1) {
            throw new Exception("Missing group ID");
        }

        String groupID = cmdArgs[0];

        String outputFormat = cmd.getOptionValue("output-format", "text");

        initializeTomcatJSS();
        String subsystem = parent.getParent().getParent().getName();
        EngineConfig cs = getEngineConfig(subsystem);
        cs.load();
        LDAPConfig ldapConfig = cs.getInternalDBConfig();

        PasswordStoreConfig psc = cs.getPasswordStoreConfig();
        IPasswordStore passwordStore = IPasswordStore.create(psc);

        LDAPConnectionConfig connConfig = ldapConfig.getConnectionConfig();

        LdapConnInfo connInfo = new LdapConnInfo(connConfig);
        LdapAuthInfo authInfo = getAuthInfo(passwordStore, connInfo, ldapConfig);

        PKISocketConfig socketConfig = cs.getSocketConfig();

        PKISocketFactory socketFactory;
        if (authInfo.getAuthType() == LdapAuthInfo.LDAP_AUTHTYPE_SSLCLIENTAUTH) {
            socketFactory = new PKISocketFactory(authInfo.getClientCertNickname());
        } else {
            socketFactory = new PKISocketFactory(connInfo.getSecure());
        }
        socketFactory.init(socketConfig);

        UGSubsystemConfig ugConfig = cs.getUGSubsystemConfig();
        UGSubsystem ugSubsystem = new UGSubsystem();

        GroupMemberCollection response = new GroupMemberCollection();

        try {
            ugSubsystem.init(socketConfig, ugConfig, passwordStore);

            Group group = ugSubsystem.getGroupFromName(groupID);

            if (group == null) {
                throw new GroupNotFoundException(groupID);
            }

            Enumeration<String> members = group.getMemberNames();
            int total = 0;

            while (members.hasMoreElements()) {
                String memberID = members.nextElement();

                GroupMemberData member = new GroupMemberData();
                member.setID(memberID);
                member.setGroupID(groupID);

                response.addEntry(member);
                total++;
            }

            response.setTotal(total);

        } finally {
            ugSubsystem.shutdown();
        }

        if (outputFormat.equalsIgnoreCase("json")) {
            System.out.println(response.toJSON());

        } else if (outputFormat.equalsIgnoreCase("text")) {

            boolean first = true;

            for (GroupMemberData member : response.getEntries()) {

                if (first) {
                    first = false;
                } else {
                    System.out.println();
                }

                System.out.println("  User ID: " + member.getID());
            }

        } else {
            throw new Exception("Unsupported output format: " + outputFormat);
        }
    }
}
