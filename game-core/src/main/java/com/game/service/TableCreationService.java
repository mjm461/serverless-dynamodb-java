package com.game.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class TableCreationService {

    @Data
    @NoArgsConstructor
    public static class CapacityUnits {
        public CapacityUnits(Long read, Long write, Projection projection){
            this.read = read;
            this.write = write;
            this.projection = projection;
        }

        private Long read;
        private Long write;
        private Projection projection;
    }

    private static Logger logger = LoggerFactory.getLogger(TableCreationService.class);

    private AmazonDynamoDB amazonDynamoDB;
    @Autowired
    public void setAmazonDynamoDB(AmazonDynamoDB amazonDynamoDB) {
        this.amazonDynamoDB = amazonDynamoDB;
    }

    public <T> void updateOrCreate(Class<T> clazz, Long readCapacityUnits, Long writeCapacityUnits, Map<String, CapacityUnits> capacityUnits) {

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(clazz);

        try {
            // check if table exists
            TableUtils.waitUntilActive(amazonDynamoDB, tableRequest.getTableName(), 1000, 100);

            // update it
            logger.info("Table for: " + clazz.getSimpleName() + " exists, checking for updates");
            updateTable(clazz, readCapacityUnits, writeCapacityUnits, capacityUnits);

        } catch (InterruptedException | TableUtils.TableNeverTransitionedToStateException e) {
            // create it
            logger.info("Table for: " + clazz.getSimpleName() + " does not exist, creating it");
            createTable(clazz, readCapacityUnits, writeCapacityUnits, capacityUnits);
        }
    }

    public <T> CreateTableResult createTable(Class<T> clazz, Long readCapacityUnits, Long writeCapacityUnits, Map<String, CapacityUnits> capacityUnits) {

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(clazz);

        readCapacityUnits = readCapacityUnits != null ? readCapacityUnits : 1L;
        writeCapacityUnits = writeCapacityUnits != null ? writeCapacityUnits : 1L;

        logger.info("Creating table: {} read: {} write: {}",
                tableRequest.getTableName(), readCapacityUnits, readCapacityUnits);

        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(readCapacityUnits, writeCapacityUnits));

        if(tableRequest.getLocalSecondaryIndexes() != null) {
            for (LocalSecondaryIndex lsi : tableRequest.getLocalSecondaryIndexes()) {

                Projection projection = new Projection().withProjectionType(ProjectionType.ALL);

                if (capacityUnits != null) {
                    CapacityUnits units = capacityUnits.getOrDefault(lsi.getIndexName(), null);

                    if (units != null) {
                        projection = units.getProjection() != null ? units.getProjection() : projection;
                    }

                }

                logger.info("Creating table: {} lsi: {} with projection: {}", tableRequest.getTableName(), lsi.getIndexName(), projection);

                lsi.setProjection(projection);
            }
        }

        if(tableRequest.getGlobalSecondaryIndexes() != null) {
            for (GlobalSecondaryIndex gsi : tableRequest.getGlobalSecondaryIndexes()) {

                Long gsIreadCapacityUnits = 1L;
                Long gsIwriteCapacityUnits = 1L;
                Projection projection = new Projection().withProjectionType(ProjectionType.ALL);

                if (capacityUnits != null) {
                    CapacityUnits units = capacityUnits.getOrDefault(gsi.getIndexName(), null);

                    if (units != null) {
                        gsIreadCapacityUnits = units.getRead() != null ? units.getRead() : gsIreadCapacityUnits;
                        gsIwriteCapacityUnits = units.getWrite() != null ? units.getWrite() : gsIwriteCapacityUnits;
                        projection = units.getProjection() != null ? units.getProjection() : projection;
                    }

                }

                logger.info("Creating table: {} gsi: {} with read: {} write: {} projection: {}",
                        tableRequest.getTableName(), gsi.getIndexName(), gsIreadCapacityUnits, gsIreadCapacityUnits, projection);

                gsi.setProvisionedThroughput(new ProvisionedThroughput(gsIreadCapacityUnits, gsIwriteCapacityUnits));
                gsi.setProjection(projection);
            }
        }

        logger.info("Commiting create table: {}", tableRequest.getTableName());

        return amazonDynamoDB.createTable(tableRequest);

    }

    public <T> UpdateTableResult updateTable(Class<T> clazz, Long readCapacityUnits, Long writeCapacityUnits, Map<String, CapacityUnits> gsiCapacityUnits){

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        String tableName = dynamoDBMapper.generateCreateTableRequest(clazz).getTableName();
        TableDescription tableDescription = amazonDynamoDB.describeTable(tableName).getTable();
        Boolean doUpdate = false;

        logger.info("Updating table: {}", tableName);

        UpdateTableRequest updateTableRequest = new UpdateTableRequest();
        updateTableRequest.setTableName(tableName);
        if(!tableDescription.getProvisionedThroughput().getReadCapacityUnits().equals(readCapacityUnits) ||
                !tableDescription.getProvisionedThroughput().getWriteCapacityUnits().equals(writeCapacityUnits)){

            logger.info("Updating table: {} read: {} write: {}", tableName, readCapacityUnits, readCapacityUnits);

            updateTableRequest.setProvisionedThroughput(new ProvisionedThroughput(readCapacityUnits, writeCapacityUnits));
            doUpdate = true;
        }

        if (gsiCapacityUnits != null) {

            List<GlobalSecondaryIndexUpdate> updates = tableDescription.getGlobalSecondaryIndexes().stream().map(description -> {

                CapacityUnits units = gsiCapacityUnits.getOrDefault(description.getIndexName(), null);

                if (units != null) {
                    Long gsIreadCapacityUnits = units.getRead() != null ? units.getRead() :
                            description.getProvisionedThroughput().getReadCapacityUnits();
                    Long gsIwriteCapacityUnits = units.getWrite() != null ? units.getWrite() :
                            description.getProvisionedThroughput().getWriteCapacityUnits();

                    UpdateGlobalSecondaryIndexAction update = new UpdateGlobalSecondaryIndexAction();
                    update.setIndexName(description.getIndexName());

                    logger.info("Updating table: {} gsi: {} with read: {} write: {}",
                            tableName, description.getIndexName(), gsIreadCapacityUnits, gsIreadCapacityUnits);

                    update.setProvisionedThroughput(new ProvisionedThroughput(gsIreadCapacityUnits, gsIwriteCapacityUnits));
                    GlobalSecondaryIndexUpdate globalSecondaryIndexUpdate = new GlobalSecondaryIndexUpdate();
                    globalSecondaryIndexUpdate.setUpdate(update);
                    return globalSecondaryIndexUpdate;
                }
                return null;

            }).filter(Objects::nonNull).collect(Collectors.toList());

            if (updates.size() > 0) {
                doUpdate = true;
                updateTableRequest.setGlobalSecondaryIndexUpdates(updates);
            }
        }

        UpdateTableResult updateTableResult = null;
        if(doUpdate) {
            logger.info("Commiting update table: {}", tableName);
            updateTableResult = amazonDynamoDB.updateTable(updateTableRequest);
        }

        return updateTableResult;

    }
}
