package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private LocacaoService service;
	
	private LocacaoDAO dao;

	private List<Filme> filmes;

	private Usuario usuario;

	@Before
	public void setup() {
		service = new LocacaoService();
		dao = mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		filmes = new ArrayList<Filme>();
		usuario = umUsuario().agora();
	}

	@Test
	public void deveAlugarFilmeComSucesso() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cen�rio
		filmes = Arrays.asList(umFilme().comValor(5.0).agora());

		// a��o
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), CoreMatchers.is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
	}

	// forma elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		// cen�rio
		filmes = Arrays.asList(umFilme().semEstoque().agora());

		// a��o
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		filmes = Arrays.asList(umFilme().agora(), umFilme().agora());

		// a��o
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {

		// cen�rio
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		// a��o
		service.alugarFilme(usuario, null);

	}

	@Test
	public void deveAplicarDesc25PorCentNoTerceiroFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cen�rio
		filmes = Arrays.asList(umFilme().agora(), umFilme().agora(),
				umFilme().agora());

		// a��o
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// valida��o
		error.checkThat(locacao.getValor(), is(CoreMatchers.equalTo(11.0)));
	}

	@Test
	public void deveAplicarDesc50PorCentNoQuartoFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cen�rio
		filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora(), umFilme().agora());

		// a��o
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// valida��o
		error.checkThat(locacao.getValor(), is(equalTo(13.0)));
	}

	@Test
	public void deveAplicarDesc75PorCentNoQuintoFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cen�rio
		filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora(),umFilme().agora(), umFilme().agora());

		// a��o
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// valida��o
		error.checkThat(locacao.getValor(), is(equalTo(14.0)));
	}

	@Test
	public void deveAplicarDesc100PorCentNoSextoFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cen�rio
		filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora(),
				umFilme().agora(), umFilme().agora(), umFilme().agora());
		
		// a��o
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// valida��o
		error.checkThat(locacao.getValor(), is(equalTo(14.0)));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cen�rio
		filmes = Arrays.asList(umFilme().agora());
		
		//a��o
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verifica��o
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() {
		//cen�rio
		
		//a��o
		
		
	}

	// forma robusta
//	@Test
//	public void testeLocacao_filmeSemEstoque_testandoMensage() {
//		// cen�rio
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Usuario 1");
//		Filme filme = new Filme("Filme 1", 0, 5.0);
//
//		// a��o
//		try {
//			service.alugarFilme(usuario, filme);
//			Assert.fail("Deveria ter lan�ado uma exce��o");
//		} catch (Exception e) {
//			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
//		}
//	}

	// forma nova
//	@Test
//	public void testeLocacao_filmeSemEstoque3() throws Exception {
//		// cen�rio
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Usuario 1");
//		Filme filme = new Filme("Filme 1", 0, 5.0);
//
//		exception.expect(Exception.class);
//		exception.expectMessage("Filme sem estoque");
//
//		// a��o
//		service.alugarFilme(usuario, filme);
//		
//	}

}
