package org.oddjob.dido.sql;

import java.sql.SQLException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.beanbus.FlushableDataWriter;
import org.oddjob.dido.layout.LayoutNode;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphProvider;
import org.oddjob.dido.morph.Morphable;
import org.oddjob.dido.tabular.ColumnLayout;

public class SQLLayout extends LayoutNode 
implements Morphable, MorphProvider, ArooaSessionAware {

	private static final Logger logger = Logger.getLogger(SQLLayout.class);
	
	private String readSQL;
	
	private String writeSQL;

	private int fetchSize; 
	
	private ArooaSession session;
	
	private MorphDefinition morph;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}

	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public Runnable morphInto(MorphDefinition morphicness) {
		
		if (childLayouts().size() > 0) {
			logger.debug("[" + this + "] has children. Morphicness ignored.");
			
			return new Runnable() {
				@Override
				public void run() {
				}
			};
		}

		String[] propertyNames = morphicness.getNames();
		
		logger.debug("[" + this + "] Morphing with properties " +
				Arrays.toString(propertyNames));
		
		for (String name : propertyNames) {
			
			ColumnLayout<Object> layout = new ColumnLayout<Object>();
			layout.setName(name);
			
			logger.debug("[" + this + "] adding morphicness [" + layout + "]");
			
			addOrRemoveChild(childLayouts().size(), layout);
		}
		
		return new Runnable() {
			
			@Override
			public void run() {
				childLayouts().clear();
			}
		};
	}
	
	@Override
	public MorphDefinition morphOf() {
		
		return morph;
	}
	
	class SQLLayoutReader implements DataReader {
		
		private final SQLDataIn sqlDataIn;
		
		private DataReader nextReader;
		
		public SQLLayoutReader(SQLDataIn sqlDataIn) {
			this.sqlDataIn = sqlDataIn;
		}
		
		@Override
		public Object read() throws DataException {

			if (nextReader == null) {
				
				try {
					if (!sqlDataIn.next()) {
						return null;
					}
				} 
				catch (SQLException e) {
					throw new DataException(e);
				}
				
				nextReader = nextReaderFor(sqlDataIn);
				
			}
			
			Object next = nextReader.read();
			if (next == null) {
				nextReader.close();
				nextReader = null;
				
				return read();
			}
			else {
				return next;
			}			
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextReader != null) {
				nextReader.close();
				nextReader = null;
			}
			
			try {
				sqlDataIn.close();
			} 
			catch (SQLException e) {
				throw new DataException(e);
			}
		}
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		ConnectionDataIn dataSourceIn = dataIn.provideDataIn(ConnectionDataIn.class);
		
		try {
			logger.debug("Creating reader for: " + readSQL);
					
			SQLDataIn sqlDataIn = new SQLDataInImpl(
					dataSourceIn.connection(), readSQL, session, fetchSize);
		
			morph = sqlDataIn.morphOf();
			
			return new SQLLayoutReader(sqlDataIn);
		} 
		catch (SQLException e) {
			throw new DataException(e);
		}		
	}
	
	class SQLLayoutWriter implements FlushableDataWriter {
	
		private final SQLDataOut sqlDataOut;
		
		private DataWriter nextWriter;
		
		public SQLLayoutWriter(SQLDataOut sqlDataOut) {
			this.sqlDataOut = sqlDataOut;
		}
		
		@Override
		public boolean write(Object object) throws DataException {
			
			if (nextWriter == null) {
				nextWriter = nextWriterFor(sqlDataOut);
			}
			
			if (nextWriter.write(object)) {
				return true;
			}
			
			if (sqlDataOut.isWrittenTo()) {
				
				sqlDataOut.resetWrittenTo();
				return write(object);
			}
			
			try {
				sqlDataOut.addBatch();
			} 
			catch (SQLException e) {
				throw new DataException(e);
			}
			
			return true;
		}

		@Override
		public void flush() throws DataException {

			try {
				sqlDataOut.execute();
			} 
			catch (SQLException e) {
				throw new DataException(e);
			}
		}
		
		@Override
		public void close() throws DataException {
			
			if (nextWriter != null) {
				nextWriter.close();
				nextWriter = null;
			}
			
			flush();
			
			try {
				sqlDataOut.close();
			} 
			catch (SQLException e) {
				throw new DataException(e);
			}
		}
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		ConnectionDataOut dataSourceIn = dataOut.provideDataOut(ConnectionDataOut.class);
		
		try {
			logger.debug("Creating writer for: " + readSQL);
			
			SQLDataOut sqlDataOut = new SQLDataOutImpl(
					dataSourceIn.connection(), writeSQL, session);
		
			return new SQLLayoutWriter(sqlDataOut);
		}
		catch (SQLException e) {
			throw new DataException(e);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		morph = null;
	}

	public String getReadSQL() {
		return readSQL;
	}

	public void setReadSQL(String sqlIn) {
		this.readSQL = sqlIn;
	}

	public String getWriteSQL() {
		return writeSQL;
	}

	public void setWriteSQL(String sqlOut) {
		this.writeSQL = sqlOut;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
}
