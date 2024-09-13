/**
 * 
 */
package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.AtributosRegras;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

/** 
 * 
 */
public class InsereRegistro implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		// Não implementado para o evento de exclusão
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
//		DynamicVO vo = (DynamicVO) event.getVo();
//		BigDecimal nroOp = vo.asBigDecimal("IDIPROC");
//	    //BigDecimal CODPRODPA = vo.asBigDecimal("CODPRODPA");
//		BigDecimal top = vo.asBigDecimal("CODTIPOPER");
//		
//		BigDecimal qntApontada = getQntApontada(nroOp);
//		if (qntApontada.compareTo(BigDecimal.ZERO) > 0) {
//		    throw new Exception("afterInsert apontamento " + qntApontada);
//		}

//		if (top == BigDecimal.valueOf(1400)) {
//	        throw new Exception("Adicionando apontamento " + top);
//	
//		}

		// String statusEtapa = consultaStatusOp(nroOp);
		// throw new Exception("Adicionando apontamento " + statusEtapa);

		// Verifica se o valor do ID da operação não é nulo
//        if(consultaStatusOp(nroOp) == "FINALIZAÇÃO") {
//	        if (nroOp != null) {
//	               throw new Exception("Adicionando apontamento" + nroOp );
//	        }
//	    }

		// BigDecimal CODPRODPA = vo.asBigDecimal("CODPRODPA");
		// BigDecimal QTDPRODUZIR = vo.asBigDecimal("QTDPRODUZIR");
		// BigDecimal ORIGEM = consultaOrigemProduto(CODPRODPA);
		// Timestamp dh = vo.asTimestamp("HRENTSAI");

		// BigDecimal ORIGEM = consultaOrigemProduto(CODPRODPA);

		// throw new Exception("Adicionando apontamento" + nroOp + nunota);

		// Verifica se o valor do ID da operação não é nulo
//	        if (nroOp != null) {
//	        	if(ORIGEM.compareTo(BigDecimal.ZERO) == 0) {
//
//	                //utils.lancaDataHoraBanco(OP);
//	        	}
//	        }

//		if ("A".equals(status)) {
//	        BigDecimal count = verificaExistenciaOp(nroOp);
//	        if (count.compareTo(BigDecimal.ZERO) == 0) {
//	            //addTelaIndProd(nroOp, dhInst);
//	        }
//		}else if ("F".equals(status)){
//			Timestamp dhFin = vo.asTimestamp("DHTERMINO");
//			//updateAddQntApontada(nroOp, dhFin);
//		}
	}

	public String consultaStatusOp(BigDecimal nroOp) throws Exception {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		String descricao = "";
		try {
			// throw new MGEModelException("" + nroOp + " " + dhInst );

			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT DISTINCT CAB.NUNOTA, EFX.DESCRICAO AS DESCRICAO FROM SANKHYA.TGFCAB CAB \r\n"
					+ "INNER JOIN SANKHYA.TPRIPROC OPE ON OPE.IDIPROC = CAB.IDIPROC\r\n"
					+ "INNER JOIN SANKHYA.TPRIATV ATV ON ATV.IDIPROC = OPE.IDIPROC\r\n"
					+ "INNER JOIN SANKHYA.TPREFX EFX ON ATV.IDEFX = ATV.IDEFX\r\n"
					+ "INNER JOIN SANKHYA.TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA\r\n"
					+ "WHERE CAB.IDIPROC = :IDIPROC AND ITE.SEQUENCIA>0 AND CAB.CODCENCUS=0");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			if (rset.next()) {
				descricao = rset.getString("DESCRICAO");

			}

		} catch (Exception e) {
			throw new Exception("Error ao validar op " + e);
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return descricao;

	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {

		DynamicVO vo = (DynamicVO) event.getVo();
		BigDecimal nroOp = vo.asBigDecimal("IDIPROC");
		BigDecimal top = vo.asBigDecimal("CODTIPOPER");
		BigDecimal nunota = vo.asBigDecimal("NUNOTA");
		BigDecimal qntApontada = getQntApontada(nroOp);
		BigDecimal CODPRODPA = getCodProduto(nroOp);
		
		if ((JapeSession.getProperty(AtributosRegras.CONFIRMANDO) != null) && top.intValue() == 1400) {
			if (qntApontada.compareTo(BigDecimal.ZERO) != 0) {
				pegarRegistros(nroOp);
				//throw new Exception("AFTERUPDATE apontamento quando TOP é " + consultaOrigemProduto(CODPRODPA));
			}
			
		}
	}
	
	
	
	private void pegarRegistros(BigDecimal nroOp) throws Exception {
		JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rset = null;
        JapeSession.SessionHandle hnd = null;
        
        
        try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT \r\n"
					+ "    Subconsulta.IDIPROC AS IDIPROC,\r\n"
					+ "    Subconsulta.NUNOTA AS NUNOTA,\r\n"
					+ "    Subconsulta.CODPRODPA AS CODPRODPA,\r\n"
					+ "    Subconsulta.HRENTSAI AS HRENTSAI,\r\n"
					+ "    SUM(Subconsulta.SOMAAPT) AS SomaQtdNeg,\r\n"
					+ "    CONVERT(date, Subconsulta.HRENTSAI) AS DATA_HRENTSAI\r\n"
					+ "FROM\r\n"
					+ "    (SELECT DISTINCT \r\n"
					+ "        ATV.IDIPROC AS IDIPROC, \r\n"
					+ "        CAB.NUNOTA AS NUNOTA, \r\n"
					+ "        IPA.CODPRODPA AS CODPRODPA, \r\n"
					+ "        CAB.HRENTSAI AS HRENTSAI,\r\n"
					+ "        ITE.QTDNEG AS SOMAAPT\r\n"
					+ "    FROM \r\n"
					+ "        SANKHYA.TPRAMP AMP\r\n"
					+ "        INNER JOIN SANKHYA.TPRAPO APO ON AMP.NUAPO = APO.NUAPO\r\n"
					+ "        INNER JOIN SANKHYA.TPRIATV ATV ON APO.IDIATV = ATV.IDIATV\r\n"
					+ "        INNER JOIN SANKHYA.TPRIPA IPA ON IPA.IDIPROC = ATV.IDIPROC\r\n"
					+ "        INNER JOIN SANKHYA.TGFPRO ON TGFPRO.CODPROD = AMP.CODPRODMP\r\n"
					+ "        INNER JOIN SANKHYA.TPREFX EFX ON ATV.IDEFX = ATV.IDEFX\r\n"
					+ "        INNER JOIN SANKHYA.TGFCAB CAB ON CAB.IDIPROC = ATV.IDIPROC\r\n"
					+ "        INNER JOIN SANKHYA.TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA\r\n"
					+ "    WHERE \r\n"
					+ "        ATV.IDIPROC = :IDIPROC \r\n"
					+ "        AND EFX.DESCRICAO = 'FINALIZACAO' \r\n"
					+ "        AND CAB.CODCENCUS = 0 \r\n"
					+ "        AND ITE.CODPROD = IPA.CODPRODPA) AS Subconsulta\r\n"
					+ "GROUP BY \r\n"
					+ "    Subconsulta.IDIPROC,\r\n"
					+ "    Subconsulta.NUNOTA,\r\n"
					+ "    Subconsulta.CODPRODPA,\r\n"
					+ "    Subconsulta.HRENTSAI,\r\n"
					+ "    CONVERT(date, Subconsulta.HRENTSAI)");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			while (rset.next()) {
				
				BigDecimal nuNota = rset.getBigDecimal("NUNOTA");
				BigDecimal CODPRODPA = rset.getBigDecimal("CODPRODPA");
				Timestamp  dh = rset.getTimestamp("HRENTSAI");
				BigDecimal qntApontada = rset.getBigDecimal("SomaQtdNeg");
				BigDecimal litragem = getLitragemProduto(CODPRODPA);
				
		        BigDecimal count = verificaExistenciaNunota(nuNota);

				 if (count.compareTo(BigDecimal.ZERO) == 0) {
						addTelaIndProd(nroOp, CODPRODPA, dh, nuNota, qntApontada, litragem);
				 }

				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            JapeSession.close(hnd);
        }
	}
	
	private void addTelaIndProd( BigDecimal nroOp, BigDecimal CODPRODPA, Timestamp dh, BigDecimal nuNota, BigDecimal qntApontada, BigDecimal litragem) throws Exception {
		JapeSession.SessionHandle hnd = null;
		JdbcWrapper jdbc = null;
		NativeSql sql = null;

		//BigDecimal codProd = getCodProduto(nroOp);
		String nomePro = getNomeProduto(CODPRODPA);

		try {
			hnd = JapeSession.open();
			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

			JapeWrapper cotDAO = JapeFactory.dao("AD_INDIPROD");
			// Cria um novo registro na tabela AD_INDIPROD
			DynamicVO cotVo = cotDAO.create()
					.set("IDIPROC", nroOp)
					.set("NUNOTA", nuNota)
					.set("DHINI", dh)
					.set("LITRAGEM", litragem)
					.set("CODPROD", CODPRODPA)
					.set("QNTAPONTAMENTO", qntApontada)
					.set("NOMEPROD", nomePro).save();
		} catch (Exception e) {
			throw new MGEModelException("Erro ao criar registro em AD_INDIPROD", e);
		} finally {
			JdbcUtils.closeResultSet(null);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
	
	

	public BigDecimal verificaExistenciaNunota(BigDecimal nuNota) throws Exception {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		BigDecimal count = BigDecimal.valueOf(0);
		try {
			// throw new MGEModelException("" + nroOp + " " + dhInst );

			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT COUNT(NUNOTA) AS CONTAGEM FROM sankhya.AD_INDIPROD WHERE NUNOTA = :NUNOTA");

			sql.setNamedParameter("NUNOTA", nuNota);

			rset = sql.executeQuery();

			if (rset.next()) {
				count = rset.getBigDecimal("CONTAGEM");

			}

		} catch (Exception e) {
			throw new Exception("Error ao validar op " + e);
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return count;

	}
	

	public BigDecimal verificaExistenciaOp(BigDecimal nroOp) throws Exception {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;
		BigDecimal count = BigDecimal.valueOf(0);
		try {
			// throw new MGEModelException("" + nroOp + " " + dhInst );

			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT COUNT(IDIPROC) AS CONTAGEM FROM sankhya.AD_INDIPROD WHERE IDIPROC = :IDIPROC");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			if (rset.next()) {
				count = rset.getBigDecimal("CONTAGEM");

			}

		} catch (Exception e) {
			throw new Exception("Error ao validar op " + e);
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return count;

	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// Não implementado antes do commit
	}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		// Não implementado para o evento de exclusão
	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		// Não implementado para o evento de inserção
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// Não implementado antes da atualização
	}

	public BigDecimal getCodProduto(BigDecimal nroOp) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		BigDecimal codProd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT DISTINCT IPA.CODPRODPA FROM SANKHYA.TGFCAB CAB\r\n"
					+ "INNER JOIN sankhya.TGFITE ITE ON ITE.NUNOTA = CAB.NUNOTA\r\n"
					+ "INNER JOIN SANKHYA.TPRIPA IPA ON IPA.IDIPROC = CAB.IDIPROC\r\n"
					+ "\r\n"
					+ "WHERE CAB.IDIPROC = :IDIPROC");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			if (rset.next()) {
				codProd = rset.getBigDecimal("CODPRODPA");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return codProd;
	}

	public String getNomeProduto(BigDecimal codProd) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		String nomeProd = "";

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT * FROM TGFPRO WHERE CODPROD = :CODPROD");

			sql.setNamedParameter("CODPROD", codProd);

			rset = sql.executeQuery();

			if (rset.next()) {
				nomeProd = rset.getString("DESCRPROD");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return nomeProd;
	}

	public BigDecimal getLitragemProduto(BigDecimal CODPRODPA) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		BigDecimal litragemProd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT * FROM TGFPRO WHERE CODPROD = :CODPROD");

			sql.setNamedParameter("CODPROD", CODPRODPA);

			rset = sql.executeQuery();

			if (rset.next()) {
				litragemProd = rset.getBigDecimal("AD_CLASSE_PROD");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return litragemProd;
	}



	public BigDecimal getQntApontada(BigDecimal nroOp) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		BigDecimal qntApontamento = BigDecimal.valueOf(0);

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT DISTINCT\r\n" + "COALESCE(\r\n" + "(SELECT SUM(ITEPA.QTDNEG) AS QTDNEGPA\r\n"
					+ "FROM SANKHYA.TPRIPROC IPROC\r\n"
					+ "INNER JOIN SANKHYA.TGFCAB CAB ON (IPROC.IDIPROC = CAB.IDIPROC)\r\n"
					+ "INNER JOIN SANKHYA.TGFITE ITEPA ON (CAB.NUNOTA = ITEPA.NUNOTA)\r\n"
					+ "INNER JOIN SANKHYA.TGFPRO PROPA ON (ITEPA.CODPROD = PROPA.CODPROD)\r\n"
					+ "WHERE ITEPA.CODLOCALORIG IN (1030000, 1170000, 1010000)\r\n"
					+ "  AND ITEPA.CODPROD = IPA.CODPRODPA\r\n"
					+ "  AND IPA.IDIPROC = CAB.IDIPROC), 0) AS SomaQtdNeg\r\n" + "\r\n" + "FROM SANKHYA.TPRAMP AMP\r\n"
					+ "INNER JOIN SANKHYA.TPRAPO APO ON AMP.NUAPO = APO.NUAPO\r\n"
					+ "INNER JOIN SANKHYA.TPRIATV ATV ON APO.IDIATV = ATV.IDIATV\r\n"
					+ "INNER JOIN SANKHYA.TPRIPA IPA ON IPA.IDIPROC = ATV.IDIPROC\r\n"
					+ "INNER JOIN SANKHYA.TGFPRO ON TGFPRO.CODPROD = AMP.CODPRODMP\r\n"
					+ "INNER JOIN SANKHYA.TPREFX EFX ON ATV.IDEFX = ATV.IDEFX\r\n" + "\r\n"
					+ "WHERE ATV.IDIPROC = :IDIPROC AND EFX.DESCRICAO = 'FINALIZACAO'");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			if (rset.next()) {
				qntApontamento = rset.getBigDecimal("SomaQtdNeg");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return qntApontamento;
	}

	public BigDecimal getQntProduzidaL(BigDecimal nroOp) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		BigDecimal qntProduzidaL = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("\r\n" + "SELECT DISTINCT\r\n" + "COALESCE(\r\n" + "	(SELECT \r\n" + "\r\n" + "	SUM\r\n"
					+ "	(CASE WHEN PROPA.AD_CLASSE_PROD = 1 THEN ITEPA.QTDNEG * 0.02\r\n"
					+ "            WHEN PROPA.AD_CLASSE_PROD = 2 THEN ITEPA.QTDNEG * 0.5\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 3 THEN ITEPA.QTDNEG * 1.5          \r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 4 THEN ITEPA.QTDNEG * 3	   \r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 5 THEN ITEPA.QTDNEG * 5\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 6 THEN ITEPA.QTDNEG * 20\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 7 THEN ITEPA.QTDNEG * 200\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 8 THEN ITEPA.QTDNEG * 1000\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 9 THEN ITEPA.QTDNEG * 1\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 10 THEN ITEPA.QTDNEG * 2.8\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 11 THEN ITEPA.QTDNEG * 0.05\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 12 THEN ITEPA.QTDNEG * 0.24\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 13 THEN ITEPA.QTDNEG * 0.12\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD = 14 THEN ITEPA.QTDNEG * 0.06\r\n"
					+ "        	WHEN PROPA.AD_CLASSE_PROD IN (0) THEN ITEPA.QTDNEG\r\n"
					+ "        END) AS 'Realizado_Litros'\r\n" + "\r\n" + "	FROM SANKHYA.TPRIPROC IPROC\r\n"
					+ "	INNER JOIN SANKHYA.TGFCAB CAB ON (IPROC.IDIPROC = CAB.IDIPROC)\r\n"
					+ "	INNER JOIN SANKHYA.TGFITE ITEPA ON (CAB.NUNOTA = ITEPA.NUNOTA)\r\n"
					+ "	INNER JOIN SANKHYA.TGFPRO PROPA ON (ITEPA.CODPROD = PROPA.CODPROD)\r\n"
					+ "	WHERE ITEPA.CODLOCALORIG IN (1030000, 1170000, 1010000)\r\n"
					+ "	  AND ITEPA.CODPROD = IPA.CODPRODPA\r\n" + "	  AND IPA.IDIPROC = CAB.IDIPROC)\r\n"
					+ ", 0) AS Litros\r\n" + "\r\n" + "\r\n" + "FROM SANKHYA.TPRAMP AMP\r\n"
					+ "INNER JOIN SANKHYA.TPRAPO APO ON AMP.NUAPO = APO.NUAPO\r\n"
					+ "INNER JOIN SANKHYA.TPRIATV ATV ON APO.IDIATV = ATV.IDIATV\r\n"
					+ "INNER JOIN SANKHYA.TPRIPA IPA ON IPA.IDIPROC = ATV.IDIPROC\r\n"
					+ "INNER JOIN SANKHYA.TGFPRO ON TGFPRO.CODPROD = AMP.CODPRODMP\r\n"
					+ "WHERE ATV.IDIPROC = :IDIPROC");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			if (rset.next()) {
				qntProduzidaL = rset.getBigDecimal("Litros");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return qntProduzidaL;
	}

	public BigDecimal getIdIndProd(BigDecimal nroOp) {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		JapeSession.SessionHandle hnd = null;

		BigDecimal idIndProd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			final EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			sql = new NativeSql(jdbc);
			sql.appendSql("SELECT INPROD FROM AD_INDIPROD PROD\r\n"
					+ "INNER JOIN TPRIPROC IPROC ON PROD.IDIPROC = IPROC.IDIPROC\r\n"
					+ "WHERE PROD.IDIPROC = :IDIPROC");

			sql.setNamedParameter("IDIPROC", nroOp);

			rset = sql.executeQuery();

			if (rset.next()) {
				idIndProd = rset.getBigDecimal("INPROD");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
		return idIndProd;
	}

	private void updateAddQntApontada(BigDecimal nroOp, Timestamp dhFin) throws Exception {
		JapeSession.SessionHandle hnd = null;
		JdbcWrapper jdbc = null;
		NativeSql query = null;

		BigDecimal idIndProd = getIdIndProd(nroOp);

		try {
			String update = "UPDATE SANKHYA.AD_INDIPROD SET QNTAPONTAMENTO = :QNTAPONTAMENTO, QNTPRODUZIDA = :QNTPRODUZIDA, DHFIN = :DHFIN WHERE INPROD = :INPROD";
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			query = new NativeSql(jdbc);
			query.setNamedParameter("INPROD", idIndProd);
			query.setNamedParameter("QNTAPONTAMENTO", getQntApontada(nroOp));
			query.setNamedParameter("QNTPRODUZIDA", getQntProduzidaL(nroOp));
			query.setNamedParameter("DHFIN", dhFin);
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
