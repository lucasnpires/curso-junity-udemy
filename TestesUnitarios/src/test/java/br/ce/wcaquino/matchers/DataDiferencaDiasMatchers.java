package br.ce.wcaquino.matchers;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DataDiferencaDiasMatchers extends TypeSafeMatcher<Date> {
	
	private Integer qtdDias;
	
	public DataDiferencaDiasMatchers(Integer qtdDias) {
		this.qtdDias = qtdDias;	
	}

	public void describeTo(Description description) {

	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(qtdDias));
	}

}
