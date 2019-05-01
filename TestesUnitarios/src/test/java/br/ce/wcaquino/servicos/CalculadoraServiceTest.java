package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraServiceTest {
	
	private CalculadoraService calc;
	
	@Before
	public void setup() {
		calc = new CalculadoraService();
	}

	@Test
	public void deveSomarDoisValores() {

		// cen�rio
		int a = 5;
		int b = 3;

		// a��o
		int resultado = calc.somar(a, b);

		// verifica��o
		Assert.assertEquals(8, resultado);
	}

	@Test
	public void deveSubtrairDoisValores() {

		// cen�rio
		int a = 8;
		int b = 5;

		// a��o
		int resultado = calc.subtrair(a, b);

		// verifica��o
		Assert.assertEquals(3, resultado);
	}

	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {

		// cen�rio
		int a = 6;
		int b = 3;

		// a��o
		int resultado = calc.dividir(a, b);

		// verifica��o
		Assert.assertEquals(2, resultado);
	}

	@Test
	public void deveMultiplicarDoisValores() {
		// cen�rio
		int a = 2;
		int b = 5;

		// a��o
		int resultado = calc.multiplicar(a, b);

		// verifica��o
		Assert.assertEquals(10, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		int a = 10;
		int b = 0;
		
		calc.dividir(a, b);
	}
}
