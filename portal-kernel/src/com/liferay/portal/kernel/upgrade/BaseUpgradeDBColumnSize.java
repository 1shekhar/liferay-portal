/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Preston Crary
 */
public abstract class BaseUpgradeDBColumnSize extends UpgradeProcess {

	public BaseUpgradeDBColumnSize(
		DBType dbType, String oldColumnType, int size) {

		_dbType = dbType;
		_oldColumnType = oldColumnType;
		_size = size;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DB db = DBManagerUtil.getDB();

		if (db.getDBType() == _dbType) {
			_upgradeTables();
		}
	}

	protected abstract void upgradeColumn(String tableName, String columnName)
		throws Exception;

	private void _upgradeTables() throws Exception {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		DBInspector dbInspector = new DBInspector(connection);

		String catalog = dbInspector.getCatalog();
		String schema = dbInspector.getSchema();

		try (LoggingTimer loggingTimer = new LoggingTimer();
			ResultSet tableRS = databaseMetaData.getTables(
				catalog, schema, null, new String[] {"TABLE"})) {

			while (tableRS.next()) {
				String tableName = tableRS.getString("TABLE_NAME");

				try (ResultSet columnRS = databaseMetaData.getColumns(
						catalog, schema, tableName, null)) {

					while (columnRS.next()) {
						int size = columnRS.getInt("COLUMN_SIZE");

						if ((size == _size) &&
							StringUtil.equalsIgnoreCase(
								_oldColumnType,
								columnRS.getString("TYPE_NAME"))) {

							String columnName = columnRS.getString(
								"COLUMN_NAME");

							try {
								upgradeColumn(tableName, columnName);
							}
							catch (SQLException sqle) {
								if (sqle.getErrorCode() == 1441) {
									if (_log.isWarnEnabled()) {
										StringBundler sb = new StringBundler(8);

										sb.append("Unable to alter length of ");
										sb.append("column ");
										sb.append(columnName);
										sb.append(" for table ");
										sb.append(tableName);
										sb.append(" because it contains ");
										sb.append("values larger than the ");
										sb.append("new column length");

										_log.warn(sb.toString());
									}
								}
								else {
									throw sqle;
								}
							}
						}
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseUpgradeDBColumnSize.class);

	private final DBType _dbType;
	private final String _oldColumnType;
	private final int _size;

}