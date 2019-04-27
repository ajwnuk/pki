// --- BEGIN COPYRIGHT BLOCK ---
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; version 2 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// (C) 2013 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---

package com.netscape.cmstools.tps.authenticator;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.dogtagpki.cli.CLI;

import com.netscape.certsrv.tps.authenticator.AuthenticatorClient;
import com.netscape.certsrv.tps.authenticator.AuthenticatorData;
import com.netscape.cmstools.cli.MainCLI;

/**
 * @author Endi S. Dewata
 */
public class AuthenticatorShowCLI extends CLI {

    public AuthenticatorCLI authenticatorCLI;

    public AuthenticatorShowCLI(AuthenticatorCLI authenticatorCLI) {
        super("show", "Show authenticator", authenticatorCLI);
        this.authenticatorCLI = authenticatorCLI;

        createOptions();
    }

    public void printHelp() {
        formatter.printHelp(getFullName() + " <Authenticator ID> [OPTIONS...]", options);
    }

    public void createOptions() {
        Option option = new Option(null, "output", true, "Output file to store authenticator properties.");
        option.setArgName("file");
        options.addOption(option);
    }

    public void execute(String[] args) throws Exception {
        // Always check for "--help" prior to parsing
        if (Arrays.asList(args).contains("--help")) {
            printHelp();
            return;
        }

        CommandLine cmd = parser.parse(options, args);

        String[] cmdArgs = cmd.getArgs();

        if (cmdArgs.length != 1) {
            throw new Exception("No Authenticator ID specified.");
        }

        String authenticatorID = args[0];
        String output = cmd.getOptionValue("output");

        AuthenticatorClient authenticatorClient = authenticatorCLI.getAuthenticatorClient();
        AuthenticatorData authenticatorData = authenticatorClient.getAuthenticator(authenticatorID);

        if (output == null) {
            MainCLI.printMessage("Authenticator \"" + authenticatorID + "\"");
            AuthenticatorCLI.printAuthenticatorData(authenticatorData, true);

        } else {
            try (PrintWriter out = new PrintWriter(new FileWriter(output))) {
                out.println(authenticatorData);
            }
            MainCLI.printMessage("Stored authenticator \"" + authenticatorID + "\" into " + output);
        }
    }
}
