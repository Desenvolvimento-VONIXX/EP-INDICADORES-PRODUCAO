package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class updateQntProduzidaL implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		 DynamicVO vo = (DynamicVO) event.getVo();

		    BigDecimal litragem = vo.asBigDecimal("LITRAGEM");
		    BigDecimal qntApontamento = vo.asBigDecimal("QNTAPONTAMENTO");
		    BigDecimal idProd = vo.asBigDecimal("INPROD");
		    
		    BigDecimal qntProduzida;
		    
		    if (litragem.compareTo(BigDecimal.valueOf(1)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(0.02));
			    updateAddQntProduzida(idProd, qntProduzida);
		    }else if (litragem.compareTo(BigDecimal.valueOf(2)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(0.5));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(3)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(1.5));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(4)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(3));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(5)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(5));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(6)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(20));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(7)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(200));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(8)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(1000));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(9)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(1));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(10)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(2.8));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(11)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(0.05));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(12)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(0.24));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(13)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(0.12));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.valueOf(14)) == 0) {
		        qntProduzida = qntApontamento.multiply(BigDecimal.valueOf(0.06));
			    updateAddQntProduzida(idProd, qntProduzida);

		    }else if (litragem.compareTo(BigDecimal.ZERO) == 0) {
		        qntProduzida = qntApontamento;
			    updateAddQntProduzida(idProd, qntProduzida);

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
	public void beforeInsert(PersistenceEvent event) throws Exception {
		
		 
		    
	}
	
	private void updateAddQntProduzida(BigDecimal idProd, BigDecimal qntProduzida) throws Exception {
	 	JapeSession.SessionHandle hnd = null;
	    JdbcWrapper jdbc = null;
	    NativeSql query = null;
	    	    
	    try {
	        String update = "UPDATE SANKHYA.AD_INDIPROD SET QNTPRODUZIDA = :QNTPRODUZIDA WHERE INPROD = :INPROD";
	        hnd = JapeSession.open();
	        hnd.setCanTimeout(false);
	        hnd.setFindersMaxRows(-1);
	        EntityFacade entity = EntityFacadeFactory.getDWFFacade();
	        jdbc = entity.getJdbcWrapper();
	        jdbc.openSession();
	        query = new NativeSql(jdbc);
	        query.setNamedParameter("INPROD", idProd);
	        query.setNamedParameter("QNTPRODUZIDA", qntProduzida );
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
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
