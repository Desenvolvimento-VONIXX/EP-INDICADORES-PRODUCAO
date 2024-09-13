package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;

public class SalvaLogAlteracao implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		
		
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		 
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		
	    DynamicVO vo = (DynamicVO) event.getVo();
		DynamicVO old = (DynamicVO) event.getOldVO();
		
		BigDecimal qntPlanejadaAntes = old.asBigDecimal("QNTPLANEJADA");
		BigDecimal qntPlanejadaDepois = vo.asBigDecimal("QNTPLANEJADA");

		if(qntPlanejadaAntes != qntPlanejadaDepois) {
			BigDecimal idProd = vo.asBigDecimal("INPROD");
			BigDecimal usuarioLogadoID = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();
			LocalDateTime  now = LocalDateTime.now();
		    Timestamp dhAlteracao = Timestamp.valueOf(now);
		    if (qntPlanejadaAntes == null) {
		    	qntPlanejadaAntes = BigDecimal.valueOf(0);
		    }
		    salvaLog(qntPlanejadaAntes, idProd, usuarioLogadoID, dhAlteracao);

			
		}     
	}
	

	private void salvaLog(BigDecimal qntPlanejadaAntes, BigDecimal idProd, BigDecimal usuarioLogadoID, Timestamp dhAlteracao) throws Exception {
	 	

		
		JapeSession.SessionHandle hnd = null;
	    JdbcWrapper jdbc = null;
	    NativeSql query = null;
	    	    
	    try {
	        String update = "UPDATE SANKHYA.AD_INDIPROD SET QNTPLANEJADALOG = :QNTPLANEJADALOG, CODUSUALTERACAO = :CODUSUALTERACAO, DHALTERACAO = :DHALTERACAO WHERE INPROD = :INPROD";
	        hnd = JapeSession.open();
	        hnd.setCanTimeout(false);
	        hnd.setFindersMaxRows(-1);
	        EntityFacade entity = EntityFacadeFactory.getDWFFacade();
	        jdbc = entity.getJdbcWrapper();
	        jdbc.openSession();
	        query = new NativeSql(jdbc);
	        query.setNamedParameter("INPROD", idProd);
	        query.setNamedParameter("QNTPLANEJADALOG", qntPlanejadaAntes );
	        query.setNamedParameter("CODUSUALTERACAO", usuarioLogadoID );
	        query.setNamedParameter("DHALTERACAO", dhAlteracao );

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

}
