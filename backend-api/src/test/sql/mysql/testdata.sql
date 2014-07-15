#------------------------------------------------------------------------------
#
# static TestData for CaroLine2
#
#------------------------------------------------------------------------------

# BatchEE tables. We create them here to not interfere with the generated DB scripts of our own app
# Those values are taken from BatchEE artifact batchee-jbatch-${batchee.version}-dbscripts.zip
# You can find this file on the maven repository.

CREATE TABLE BATCH_CHECKPOINT (id VARCHAR(255) NOT NULL, data BLOB, stepName VARCHAR(255), type VARCHAR(20), INSTANCE_JOBINSTANCEID BIGINT, PRIMARY KEY (id)) ENGINE = innodb;
CREATE TABLE BATCH_JOBEXECUTION (executionId BIGINT NOT NULL, batchStatus VARCHAR(20), createTime DATETIME, endTime DATETIME, exitStatus VARCHAR(255), jobProperties TEXT, startTime DATETIME, updateTime DATETIME, INSTANCE_JOBINSTANCEID BIGINT, PRIMARY KEY (executionId)) ENGINE = innodb;
CREATE TABLE BATCH_JOBINSTANCE (jobInstanceId BIGINT NOT NULL, batchStatus VARCHAR(20), exitStatus VARCHAR(255), jobXml TEXT, latestExecution BIGINT, name VARCHAR(255), restartOn VARCHAR(255), step VARCHAR(255), tag VARCHAR(255), PRIMARY KEY (jobInstanceId)) ENGINE = innodb;
CREATE TABLE BATCH_STEPEXECUTION (id BIGINT NOT NULL, batchStatus VARCHAR(20), exec_commit BIGINT, endTime DATETIME, exitStatus VARCHAR(255), exec_filter BIGINT, lastRunStepExecutionId BIGINT, numPartitions INTEGER, persistentData BLOB, exec_processskip BIGINT, exec_read BIGINT, exec_readskip BIGINT, exec_rollback BIGINT, startCount INTEGER, startTime DATETIME, stepName VARCHAR(255), exec_write BIGINT, exec_writeskip BIGINT, EXECUTION_EXECUTIONID BIGINT, PRIMARY KEY (id)) ENGINE = innodb;
CREATE TABLE OPENJPA_SEQUENCE_TABLE (ID TINYINT NOT NULL, SEQUENCE_VALUE BIGINT, PRIMARY KEY (ID)) ENGINE = innodb;
CREATE INDEX I_BTCHPNT_INSTANCE ON BATCH_CHECKPOINT (INSTANCE_JOBINSTANCEID);
CREATE INDEX I_BTCHCTN_INSTANCE ON BATCH_JOBEXECUTION (INSTANCE_JOBINSTANCEID);
CREATE INDEX I_BTCHCTN_EXECUTION ON BATCH_STEPEXECUTION (EXECUTION_EXECUTIONID);

