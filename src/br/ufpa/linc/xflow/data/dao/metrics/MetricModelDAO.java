package br.ufpa.linc.xflow.data.dao.metrics;

import java.util.List;

import br.ufpa.linc.xflow.data.dao.BaseDAO;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricValuesTable;

public abstract class MetricModelDAO<MetricModelTable extends MetricValuesTable> extends BaseDAO<MetricModelTable>{

	abstract public List<MetricModelTable> getAllMetricsTable(Metrics metrics) throws DatabaseException;
	abstract public List<MetricModelTable> getMetricsTableByAuthor(Metrics metrics, Author author) throws DatabaseException;
	abstract public List<MetricModelTable> getMetricsTableFromAuthorByEntries(Metrics metrics, Author author, Entry initialEntry, Entry finalEntry) throws DatabaseException;
	
}
