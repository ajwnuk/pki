//--- BEGIN COPYRIGHT BLOCK ---
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License along
//with this program; if not, write to the Free Software Foundation, Inc.,
//51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
//(C) 2012 Red Hat, Inc.
//All rights reserved.
//--- END COPYRIGHT BLOCK ---
package com.netscape.certsrv.profile;

import javax.ws.rs.core.Response;

import com.netscape.certsrv.client.Client;
import com.netscape.certsrv.client.PKIClient;

/**
 * @author Ade Lee
 */
public class ProfileClient extends Client {

    public ProfileResource profileClient;

    public ProfileClient(PKIClient client, String subsystem) throws Exception {
        super(client, subsystem, "profile");
        init();
    }

    public void init() throws Exception {
        profileClient = createProxy(ProfileResource.class);
    }

    public ProfileData retrieveProfile(String id) throws Exception {
        Response response = profileClient.retrieveProfile(id);
        return client.getEntity(response, ProfileData.class);
    }

    public byte[] retrieveProfileRaw(String id) throws Exception {
        Response response = profileClient.retrieveProfileRaw(id);
        return client.getEntity(response, byte[].class);
    }

    public ProfileDataInfos listProfiles(Integer start, Integer size) throws Exception {
        Response response =  profileClient.listProfiles(start, size);
        return client.getEntity(response, ProfileDataInfos.class);
    }

    public void enableProfile(String id) throws Exception {
        Response response = profileClient.modifyProfileState(id, "enable");
        client.getEntity(response, Void.class);
    }

    public void disableProfile(String id) throws Exception {
        Response response = profileClient.modifyProfileState(id, "disable");
        client.getEntity(response, Void.class);
    }

    public ProfileData createProfile(ProfileData data) throws Exception {
        String createProfileRequest = (String) client.marshall(data);
        Response response = profileClient.createProfile(createProfileRequest);
        return client.getEntity(response, ProfileData.class);
    }

    public byte[] createProfileRaw(byte[] properties) throws Exception {
        Response response =
            profileClient.createProfileRaw(properties);
        return client.getEntity(response, byte[].class);
    }

    public ProfileData modifyProfile(ProfileData data) throws Exception {
        String modifyProfileRequest = (String) client.marshall(data);
        Response response = profileClient.modifyProfile(data.getId(), modifyProfileRequest);
        return client.getEntity(response, ProfileData.class);
    }

    public byte[] modifyProfileRaw(String profileId, byte[] properties) throws Exception {
        Response response = profileClient.modifyProfileRaw(profileId, properties);
        return client.getEntity(response, byte[].class);
    }

    public void deleteProfile(String id) throws Exception {
        Response response = profileClient.deleteProfile(id);
        client.getEntity(response, Void.class);
    }

}
