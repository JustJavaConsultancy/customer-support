import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICategory } from 'app/shared/model/category.model';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { ICustomer } from 'app/shared/model/customer.model';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { IIssue } from 'app/shared/model/issue.model';
import { ISSUESTATUS } from 'app/shared/model/enumerations/issuestatus.model';
import { CLASSIFICATION } from 'app/shared/model/enumerations/classification.model';
import { ENTRYCHANNEL } from 'app/shared/model/enumerations/entrychannel.model';
import { getEntity, updateEntity, createEntity, reset } from './issue.reducer';

export const IssueUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categories = useAppSelector(state => state.gateway.category.entities);
  const customers = useAppSelector(state => state.gateway.customer.entities);
  const issueEntity = useAppSelector(state => state.gateway.issue.entity);
  const loading = useAppSelector(state => state.gateway.issue.loading);
  const updating = useAppSelector(state => state.gateway.issue.updating);
  const updateSuccess = useAppSelector(state => state.gateway.issue.updateSuccess);
  const iSSUESTATUSValues = Object.keys(ISSUESTATUS);
  const cLASSIFICATIONValues = Object.keys(CLASSIFICATION);
  const eNTRYCHANNELValues = Object.keys(ENTRYCHANNEL);

  const handleClose = () => {
    navigate('/issue' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCategories({}));
    dispatch(getCustomers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...issueEntity,
      ...values,
      category: categories.find(it => it.id.toString() === values.category.toString()),
      customer: customers.find(it => it.id.toString() === values.customer.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          status: 'NEW',
          classification: 'COMPLAINT',
          entryChannel: 'WHATSAPP',
          ...issueEntity,
          category: issueEntity?.category?.id,
          customer: issueEntity?.customer?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.issue.home.createOrEditLabel" data-cy="IssueCreateUpdateHeading">
            <Translate contentKey="gatewayApp.issue.home.createOrEditLabel">Create or edit a Issue</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="issue-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.issue.createdDate')}
                id="issue-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="date"
              />
              <ValidatedField
                label={translate('gatewayApp.issue.description')}
                id="issue-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField label={translate('gatewayApp.issue.status')} id="issue-status" name="status" data-cy="status" type="select">
                {iSSUESTATUSValues.map(iSSUESTATUS => (
                  <option value={iSSUESTATUS} key={iSSUESTATUS}>
                    {translate('gatewayApp.ISSUESTATUS.' + iSSUESTATUS)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('gatewayApp.issue.classification')}
                id="issue-classification"
                name="classification"
                data-cy="classification"
                type="select"
              >
                {cLASSIFICATIONValues.map(cLASSIFICATION => (
                  <option value={cLASSIFICATION} key={cLASSIFICATION}>
                    {translate('gatewayApp.CLASSIFICATION.' + cLASSIFICATION)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('gatewayApp.issue.entryChannel')}
                id="issue-entryChannel"
                name="entryChannel"
                data-cy="entryChannel"
                type="select"
              >
                {eNTRYCHANNELValues.map(eNTRYCHANNEL => (
                  <option value={eNTRYCHANNEL} key={eNTRYCHANNEL}>
                    {translate('gatewayApp.ENTRYCHANNEL.' + eNTRYCHANNEL)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="issue-category"
                name="category"
                data-cy="category"
                label={translate('gatewayApp.issue.category')}
                type="select"
              >
                <option value="" key="0" />
                {categories
                  ? categories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="issue-customer"
                name="customer"
                data-cy="customer"
                label={translate('gatewayApp.issue.customer')}
                type="select"
              >
                <option value="" key="0" />
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/issue" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default IssueUpdate;
