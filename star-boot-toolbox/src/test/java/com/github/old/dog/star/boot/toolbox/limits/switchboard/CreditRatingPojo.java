package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import java.util.function.BiConsumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * POJO класс для представления кредитного рейтинга ценной бумаги.
 * Реализует интерфейс {@link CreditRating} и предоставляет данные о рейтинге эмитента,
 * включая информацию о ценной бумаге, эмитенте, рейтинге и кредитном агентстве.
 * <p>
 * Класс является immutable и предоставляет утилитные методы для создания
 * новых экземпляров с измененными значениями полей.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRatingPojo implements CreditRating {

    /**
     * ISIN код ценной бумаги
     */
    private String isin;

    /**
     * Наименование объекта наблюдения
     */
    private String objectName;

    /**
     * Наименование организации-эмитента
     */
    private String issuerName;

    /**
     * Название сектора экономики эмитента
     */
    private String sector;

    /**
     * Присвоенный рейтинг
     */
    private String scale;

    /**
     * Статус рейтинга
     */
    private String state;

    /**
     * Прогноз рейтинга
     */
    private String outlook;

    /**
     * Код кредитного агентства
     */
    private String creditAgency;

    // Утилитные методы для immutable изменений
    public CreditRating transform(BiConsumer<CreditRating, CreditRatingPojoBuilder> changer) {
        CreditRatingPojoBuilder builder = CreditRatingPojo.builder();
        changer.accept(this, builder);
        return builder.build();
    }

    /**
     * Создает новый экземпляр с измененным ISIN кодом
     *
     * @param newIsin новый ISIN код ценной бумаги
     * @return новый экземпляр CreditRatingImpl с обновленным ISIN
     */
    public CreditRatingPojo withIsin(String newIsin) {
        return CreditRatingPojo.builder()
            .isin(newIsin)
            .issuerName(this.issuerName)
            .sector(this.sector)
            .scale(this.scale)
            .state(this.state)
            .outlook(this.outlook)
            .creditAgency(this.creditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененным наименованием эмитента
     *
     * @param newIssuerName новое наименование организации-эмитента
     * @return новый экземпляр CreditRatingImpl с обновленным наименованием
     */
    public CreditRatingPojo withIssuerName(String newIssuerName) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(newIssuerName)
            .sector(this.sector)
            .scale(this.scale)
            .state(this.state)
            .outlook(this.outlook)
            .creditAgency(this.creditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененным сектором экономики
     *
     * @param newSector новое название сектора экономики
     * @return новый экземпляр CreditRatingImpl с обновленным сектором
     */
    public CreditRatingPojo withSector(String newSector) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(this.issuerName)
            .sector(newSector)
            .scale(this.scale)
            .state(this.state)
            .outlook(this.outlook)
            .creditAgency(this.creditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененным значением рейтинга
     *
     * @param newScale новое значение рейтинга
     * @return новый экземпляр CreditRatingImpl с обновленным рейтингом
     */
    public CreditRatingPojo withScale(String newScale) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(this.issuerName)
            .sector(this.sector)
            .scale(newScale)
            .state(this.state)
            .outlook(this.outlook)
            .creditAgency(this.creditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененным статусом рейтинга
     *
     * @param newState новый статус рейтинга
     * @return новый экземпляр CreditRatingImpl с обновленным статусом
     */
    public CreditRatingPojo withState(String newState) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(this.issuerName)
            .sector(this.sector)
            .scale(this.scale)
            .state(newState)
            .outlook(this.outlook)
            .creditAgency(this.creditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененным прогнозом рейтинга
     *
     * @param newOutlook новый прогноз рейтинга
     * @return новый экземпляр CreditRatingImpl с обновленным прогнозом
     */
    public CreditRatingPojo withOutlook(String newOutlook) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(this.issuerName)
            .sector(this.sector)
            .scale(this.scale)
            .state(this.state)
            .outlook(newOutlook)
            .creditAgency(this.creditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененным кодом кредитного агентства
     *
     * @param newCreditAgency новый код кредитного агентства
     * @return новый экземпляр CreditRatingImpl с обновленным кодом агентства
     */
    public CreditRatingPojo withCreditAgency(String newCreditAgency) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(this.issuerName)
            .sector(this.sector)
            .scale(this.scale)
            .state(this.state)
            .outlook(this.outlook)
            .creditAgency(newCreditAgency)
            .build();
    }

    /**
     * Создает новый экземпляр с измененными несколькими полями
     *
     * @param newScale   новое значение рейтинга
     * @param newState   новый статус рейтинга
     * @param newOutlook новый прогноз рейтинга
     * @return новый экземпляр CreditRatingImpl с обновленными полями рейтинга
     */
    public CreditRatingPojo withRatingInfo(String newScale, String newState, String newOutlook) {
        return CreditRatingPojo.builder()
            .isin(this.isin)
            .issuerName(this.issuerName)
            .sector(this.sector)
            .scale(newScale)
            .state(newState)
            .outlook(newOutlook)
            .creditAgency(this.creditAgency)
            .build();
    }
}
