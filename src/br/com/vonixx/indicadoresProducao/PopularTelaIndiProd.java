package br.com.vonixx.indicadoresProducao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class PopularTelaIndiProd implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao ctx) throws Exception {
		JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rset = null;
        JapeSession.SessionHandle hnd = null;
		//if(JapeSession.getProperty(AtributosRegras.CONFIRMANDO) != null ){
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
						+ "    SUM(Subconsulta.SOMAAPT) AS SomaQtdNeg\r\n"
						+ " \r\n"
						+ "\r\n"
						+ "FROM\r\n"
						+ "    (SELECT DISTINCT\r\n"
						+ "        ATV.IDIPROC AS IDIPROC, \r\n"
						+ "        CAB.NUNOTA AS NUNOTA, \r\n"
						+ "        IPA.CODPRODPA AS CODPRODPA, \r\n"
						+ "        CAB.HRENTSAI AS HRENTSAI,\r\n"
						+ "        ITE.QTDNEG AS SOMAAPT\r\n"
						+ "\r\n"
						+ "    FROM \r\n"
						+ "        SANKHYA.TPRAMP AMP\r\n"
						+ "        INNER JOIN SANKHYA.TPRAPO APO ON AMP.NUAPO = APO.NUAPO\r\n"
						+ "        INNER JOIN SANKHYA.TPRIATV ATV ON APO.IDIATV = ATV.IDIATV\r\n"
						+ "        INNER JOIN SANKHYA.TPRIPA IPA ON IPA.IDIPROC = ATV.IDIPROC\r\n"
						+ "        INNER JOIN SANKHYA.TGFPRO PRO ON PRO.CODPROD = AMP.CODPRODMP\r\n"
						+ "        INNER JOIN SANKHYA.TPREFX EFX ON ATV.IDEFX = ATV.IDEFX\r\n"
						+ "        INNER JOIN SANKHYA.TGFCAB CAB ON CAB.IDIPROC = ATV.IDIPROC\r\n"
						+ "        INNER JOIN SANKHYA.TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA\r\n"
						+ "		LEFT JOIN sankhya.AD_INDIPROD ON ITE.NUNOTA = AD_INDIPROD.NUNOTA\r\n"
						+ "    WHERE \r\n"
						+ "         EFX.DESCRICAO = 'FINALIZACAO' \r\n"
						+ "        AND CAB.CODCENCUS = 0 \r\n"
						+ "        AND ITE.CODPROD = IPA.CODPRODPA\r\n"
						+ "		AND AD_INDIPROD.NUNOTA IS NULL\r\n"
						+ "		) AS Subconsulta\r\n"
						+ "		\r\n"
						+ "GROUP BY \r\n"
						+ "    Subconsulta.IDIPROC,\r\n"
						+ "    Subconsulta.NUNOTA,\r\n"
						+ "    Subconsulta.CODPRODPA,\r\n"
						+ "    Subconsulta.HRENTSAI\r\n"
						+ "\r\n"
						+ "ORDER BY HRENTSAI DESC");
	
	
				rset = sql.executeQuery();
	
				while (rset.next()) {
					
					BigDecimal nroOp = rset.getBigDecimal("IDIPROC");
					BigDecimal nuNota = rset.getBigDecimal("NUNOTA");
					BigDecimal CODPRODPA = rset.getBigDecimal("CODPRODPA");
					Timestamp  dh = rset.getTimestamp("HRENTSAI");
					BigDecimal qntApontada = rset.getBigDecimal("SomaQtdNeg");
					BigDecimal litragem = getLitragemProduto(CODPRODPA);
			        //BigDecimal count = verificaExistenciaNunota(nuNota);
					
					addTelaIndProd(nroOp, CODPRODPA, dh, nuNota, qntApontada, litragem);
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



}
