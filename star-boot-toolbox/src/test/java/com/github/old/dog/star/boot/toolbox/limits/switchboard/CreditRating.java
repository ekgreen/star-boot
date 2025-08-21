package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import com.github.old.dog.star.boot.toolbox.strings.Strings;
import java.io.Serializable;


/**
 * Интерфейс, представляющий кредитный рейтинг с основными атрибутами, связанными с рейтингом
 * ценной бумаги или объекта наблюдения.
 */
public interface CreditRating extends Serializable {

    /**
     * Возвращает ISIN ценной бумаги
     *
     * @return код ISIN
     */
    String getIsin();

    /**
     * Возвращает наименование объекта наблюдения
     *
     * @return наименование объекта наблюдения
     */
    String getObjectName();

    /**
     * Возвращает наименование эмитента
     *
     * @return наименование организации-эмитента
     */
    String getIssuerName();

    /**
     * Возвращает сектор экономики эмитента
     *
     * @return название сектора
     */
    String getSector();

    /**
     * Возвращает значение рейтинга
     *
     * @return присвоенный рейтинг
     */
    String getScale();

    /**
     * Возвращает текущий статус рейтинга
     *
     * @return статус рейтинга
     */
    String getState();

    /**
     * Возвращает текущий прогноз рейтинга
     *
     * @return прогноз рейтинга
     */
    String getOutlook();

    /**
     * Возвращает код кредитного агентства выставившего рейтинг
     *
     * @return код агентства
     */
    String getCreditAgency();


    default String getObjectCode() {
        return this.createCreditObjectKey(this.getObjectName());
    }

    default String getIssuerCode() {
        return this.createCreditObjectKey(this.getIssuerName());
    }

    default String createCreditObjectKey(String creditObjectName) {
        if (Strings.isEmpty(creditObjectName)) {
            return null;
        }

        return Strings
            .chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, creditObjectName)
            .transliterate(Strings.Languages.ENGLISH, Strings.Options.Include.DIGITS | Strings.Options.Include.WHITESPACE)
            .changeCase(Strings.TextCase.KEBAB_CASE)
            .end();
    }

}
