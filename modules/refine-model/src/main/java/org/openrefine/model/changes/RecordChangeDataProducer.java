
package org.openrefine.model.changes;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.openrefine.model.ColumnId;
import org.openrefine.model.ColumnModel;
import org.openrefine.model.Record;

/**
 * A function which computes change data to be persisted to disk, to be later joined back to the project to produce the
 * new grid. This data might be serialized because it is volatile or expensive to compute. This is the record-wise
 * equivalent to {@link RowChangeDataProducer}.
 *
 * @param <T>
 */
public interface RecordChangeDataProducer<T> extends Serializable {

    /**
     * Compute the change data on a given record.
     */
    public T call(Record record, ColumnModel columnModel);

    /**
     * Compute the change data on a batch of consecutive records. This defaults to individual calls if the method is not
     * overridden.
     *
     * @param records
     *            the list of records to fetch change data on
     * @param columnModel
     * @return a list of the same size
     */
    public default List<T> callRecordBatch(List<Record> records, ColumnModel columnModel) {
        return records.stream()
                .map(record -> call(record, columnModel))
                .collect(Collectors.toList());
    }

    /**
     * The size of batches this producer would like to be called on. Smaller batches can be submitted (for instance at
     * the end of a partition). Defaults to 1.
     */
    public default int getBatchSize() {
        return 1;
    }

    /**
     * The maximum number of concurrent calls to this change data producer. If 0, there is no limit to the concurrency.
     */
    public default int getMaxConcurrency() {
        return 0;
    }

    /**
     * The columns this producer depends on. If null is returned, we assume that it can rely on any columns. If a set of
     * columns is returned, the producer is only allowed to read columns that are listed in this set: the other columns
     * it is fed with might not reflect the state of the grid on which the producer was meant to be applied.
     */
    public default List<ColumnId> getColumnDependencies() {
        return null;
    }
}
