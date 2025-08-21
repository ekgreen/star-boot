package com.github.old.dog.star.boot.data.access.spring.jooq.additional;

import com.github.old.dog.star.boot.data.access.jooq.api.JooqObjectRepository;
import com.github.old.dog.star.boot.data.access.spring.jooq.generated.tables.daos.CrudCreditObjectRepository;
import com.github.old.dog.star.boot.data.access.spring.jooq.generated.tables.pojos.CreditObjectPojo;
import com.github.old.dog.star.boot.data.access.spring.jooq.generated.tables.records.CreditObjectRecord;

public interface ProxyTestCreditObjectObjectRepository
        extends JooqObjectRepository<CreditObjectRecord, CreditObjectPojo, Long, CrudCreditObjectRepository> {
}
