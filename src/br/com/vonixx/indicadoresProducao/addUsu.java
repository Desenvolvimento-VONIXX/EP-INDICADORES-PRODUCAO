package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class addUsu implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO) event.getVo();
	    BigDecimal idMestre = vo.asBigDecimal("ID");
		BigDecimal usuarioLogadoID = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID();
		if(usuarioLogadoID != null) {
	    	adicionarUsu(idMestre, usuarioLogadoID);

	    }
	}
	
	private void adicionarUsu(BigDecimal idMestre, BigDecimal usuarioLogadoID) throws Exception {
	 	JapeSession.SessionHandle hnd = null;
	    JdbcWrapper jdbc = null;
	    NativeSql query = null;
	    	    
	    try {
	        String update = "UPDATE SANKHYA.AD_EFEPROD SET CODUSU = :CODUSU WHERE ID = :ID";
	        hnd = JapeSession.open();
	        hnd.setCanTimeout(false);
	        hnd.setFindersMaxRows(-1);
	        EntityFacade entity = EntityFacadeFactory.getDWFFacade();
	        jdbc = entity.getJdbcWrapper();
	        jdbc.openSession();
	        query = new NativeSql(jdbc);
	        query.setNamedParameter("ID", idMestre);
	        query.setNamedParameter("CODUSU", usuarioLogadoID );
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
