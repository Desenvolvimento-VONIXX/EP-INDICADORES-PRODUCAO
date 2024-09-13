package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class addData implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO) event.getVo();
		
	    BigDecimal idDetalhe = vo.asBigDecimal("ID_INDI");
	    BigDecimal idMestre = vo.asBigDecimal("ID");

	    Timestamp data = getData(idMestre, idDetalhe);
	    if(data != null) {
	    	adicionarData(idDetalhe, idMestre, data);
		    //throw new Exception(" " + data + " " + idMestre + " " + idDetalhe);

	    }
		
	}
	
	public Timestamp getData(BigDecimal idMestre, BigDecimal idDetalhe) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		java.sql.Timestamp data = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT AD_EFEPROD.DH AS DATA\r\n"
					+ "FROM SANKHYA.AD_EFEPROD \r\n"
					+ "INNER JOIN SANKHYA.AD_EFEPRODINDI ON AD_EFEPRODINDI.ID = AD_EFEPROD.ID\r\n"
					+ "WHERE AD_EFEPRODINDI.ID = :ID");

			sql.setNamedParameter("ID", idMestre);

			rset = sql.executeQuery();

			if (rset.next()) {
				data = rset.getTimestamp("DATA");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return data;
	}
	
	private void adicionarData(BigDecimal idDetalhe, BigDecimal idMestre, Timestamp data) throws Exception {
	 	JapeSession.SessionHandle hnd = null;
	    JdbcWrapper jdbc = null;
	    NativeSql query = null;
	    	    
	    try {
	        String update = "UPDATE SANKHYA.AD_EFEPRODINDI SET DATA = :DATA WHERE ID = :IDPROD";
	        hnd = JapeSession.open();
	        hnd.setCanTimeout(false);
	        hnd.setFindersMaxRows(-1);
	        EntityFacade entity = EntityFacadeFactory.getDWFFacade();
	        jdbc = entity.getJdbcWrapper();
	        jdbc.openSession();
	        query = new NativeSql(jdbc);
	        query.setNamedParameter("IDPROD", idMestre);
	        query.setNamedParameter("DATA", data );
	        query.appendSql(update);
	        query.executeUpdate();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new Exception("Erro ao executar a atualização: " + e.getMessage());
	    } finally {
	        JapeSession.close(hnd);
	        JdbcWrapper.closeSession(jdbc);
	        NativeSql.releaseResources(query);
	    }

	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
