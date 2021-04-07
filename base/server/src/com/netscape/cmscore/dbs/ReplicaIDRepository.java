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
// (C) 2007 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---
package com.netscape.cmscore.dbs;

import java.math.BigInteger;

import com.netscape.certsrv.base.EBaseException;
import com.netscape.cmscore.apps.DatabaseConfig;

/**
 * A class represents a replica repository. It
 * creates unique managed replica IDs.
 * <P>
 *
 * @author alee
 * @version $Revision$, $Date$
 */
public class ReplicaIDRepository extends Repository {

    public static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ReplicaIDRepository.class);

    /**
     * Constructs a certificate repository.
     */
    public ReplicaIDRepository(DBSubsystem dbSubsystem, int increment) throws EBaseException {

        super(
                dbSubsystem,
                increment,
                "ou=Replica," + dbSubsystem.getBaseDN(),
                10);

        DatabaseConfig dbConfig = dbSubsystem.getDBConfigStore();

        repositoryConfig.put(DBSubsystem.PROP_BASEDN, dbConfig.getReplicaDN());
        repositoryConfig.put(DBSubsystem.PROP_RANGE_DN, dbConfig.getReplicaRangeDN());

        repositoryConfig.put(DBSubsystem.PROP_MIN_NAME, DBSubsystem.PROP_MIN_REPLICA_NUMBER);
        repositoryConfig.put(DBSubsystem.PROP_MIN, dbConfig.getBeginReplicaNumber());

        repositoryConfig.put(DBSubsystem.PROP_MAX_NAME, DBSubsystem.PROP_MAX_REPLICA_NUMBER);
        repositoryConfig.put(DBSubsystem.PROP_MAX, dbConfig.getEndReplicaNumber());

        repositoryConfig.put(DBSubsystem.PROP_NEXT_MIN_NAME, DBSubsystem.PROP_NEXT_MIN_REPLICA_NUMBER);
        repositoryConfig.put(DBSubsystem.PROP_NEXT_MIN, dbConfig.getNextBeginReplicaNumber());

        repositoryConfig.put(DBSubsystem.PROP_NEXT_MAX_NAME, DBSubsystem.PROP_NEXT_MAX_REPLICA_NUMBER);
        repositoryConfig.put(DBSubsystem.PROP_NEXT_MAX, dbConfig.getNextEndReplicaNumber());

        repositoryConfig.put(DBSubsystem.PROP_LOW_WATER_MARK_NAME, DBSubsystem.PROP_REPLICA_LOW_WATER_MARK);
        repositoryConfig.put(DBSubsystem.PROP_LOW_WATER_MARK, dbConfig.getReplicaLowWaterMark());

        repositoryConfig.put(DBSubsystem.PROP_INCREMENT_NAME, DBSubsystem.PROP_REPLICA_INCREMENT);
        repositoryConfig.put(DBSubsystem.PROP_INCREMENT, dbConfig.getReplicaIncrement());
    }

    /**
     * Returns last serial number in given range
     */
    public BigInteger getLastSerialNumberInRange(BigInteger serial_low_bound, BigInteger serial_upper_bound)
            throws EBaseException {
        logger.debug("ReplicaIDReposoitory: in getLastSerialNumberInRange: low "
                + serial_low_bound + " high " + serial_upper_bound);
        if (serial_low_bound == null
                || serial_upper_bound == null || serial_low_bound.compareTo(serial_upper_bound) >= 0) {
            return null;
        }
        BigInteger ret = new BigInteger(getMinSerial());
        if ((ret == null) || (ret.compareTo(serial_upper_bound) > 0) || (ret.compareTo(serial_low_bound) < 0)) {
            return null;
        }
        return ret;
    }
}
