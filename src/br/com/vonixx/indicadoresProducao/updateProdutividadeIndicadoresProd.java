package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;

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

public class updateProdutividadeIndicadoresProd implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
	    
	}
//	public void afterUpdate(PersistenceEvent event) throws Exception {
//	    DynamicVO vo = (DynamicVO) event.getVo();
//
//	    BigDecimal qntPlanejada = vo.asBigDecimal("QNTPLANEJADA");
//	    BigDecimal qntProduzidaL = vo.asBigDecimal("QNTPRODUZIDA");
//	    BigDecimal idProd = vo.asBigDecimal("INPROD");
//	    
//	    if (qntPlanejada.compareTo(BigDecimal.ZERO) == 0 || qntPlanejada == null) {
//	    	 BigDecimal produtividade = BigDecimal.valueOf(0);
//		     updateAddQntApontada(idProd, produtividade);
//	       
//	    } else {
//	    	 BigDecimal produtividade = qntProduzidaL.multiply(BigDecimal.valueOf(100)).divide(qntPlanejada, 2, RoundingMode.HALF_UP);
//		     updateAddQntApontada(idProd, produtividade);
//
//	    }
//	}

	private void updateAddQntApontada(BigDecimal idProd, BigDecimal produtividade) throws Exception {
	 	JapeSession.SessionHandle hnd = null;
	    JdbcWrapper jdbc = null;
	    NativeSql query = null;
	    	    
	    try {
	        String update = "UPDATE SANKHYA.AD_INDIPROD SET PRODUTIVIDADE = :PRODUTIVIDADE WHERE INPROD = :INPROD";
	        hnd = JapeSession.open();
	        hnd.setCanTimeout(false);
	        hnd.setFindersMaxRows(-1);
	        EntityFacade entity = EntityFacadeFactory.getDWFFacade();
	        jdbc = entity.getJdbcWrapper();
	        jdbc.openSession();
	        query = new NativeSql(jdbc);
	        query.setNamedParameter("INPROD", idProd);
	        query.setNamedParameter("PRODUTIVIDADE", produtividade );
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
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		DynamicVO vo = (DynamicVO) event.getVo();
		DynamicVO old = (DynamicVO) event.getOldVO();

	    BigDecimal qntPlanejadaBefore = old.asBigDecimal("QNTPLANEJADA");
	    BigDecimal qntPlanejadaAfter = vo.asBigDecimal("QNTPLANEJADA");
	    BigDecimal qntProduzidaL = vo.asBigDecimal("QNTPRODUZIDA");
	    BigDecimal idProd = vo.asBigDecimal("INPROD");
	    
	    
	    if ((qntPlanejadaAfter != null && !qntPlanejadaAfter.equals(qntPlanejadaBefore)) || (qntPlanejadaBefore != null && !qntPlanejadaBefore.equals(qntPlanejadaAfter))) {
	        if (qntPlanejadaAfter == null || qntPlanejadaAfter.compareTo(BigDecimal.ZERO) == 0 || qntProduzidaL.compareTo(BigDecimal.ZERO) == 0 || qntProduzidaL == null) {
	            BigDecimal produtividade = BigDecimal.valueOf(0);
	            updateAddQntApontada(idProd, produtividade);
	        } else {
	            BigDecimal produtividade = qntProduzidaL.multiply(BigDecimal.valueOf(100)).divide(qntPlanejadaAfter, 2, RoundingMode.HALF_UP);
	            updateAddQntApontada(idProd, produtividade);
	        }
	    }
		
	}

}
