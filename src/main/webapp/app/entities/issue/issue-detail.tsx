import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './issue.reducer';

export const IssueDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const issueEntity = useAppSelector(state => state.gateway.issue.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="issueDetailsHeading">
          <Translate contentKey="gatewayApp.issue.detail.title">Issue</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{issueEntity.id}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="gatewayApp.issue.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {issueEntity.createdDate ? <TextFormat value={issueEntity.createdDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="description">
              <Translate contentKey="gatewayApp.issue.description">Description</Translate>
            </span>
          </dt>
          <dd>{issueEntity.description}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gatewayApp.issue.status">Status</Translate>
            </span>
          </dt>
          <dd>{issueEntity.status}</dd>
          <dt>
            <span id="classification">
              <Translate contentKey="gatewayApp.issue.classification">Classification</Translate>
            </span>
          </dt>
          <dd>{issueEntity.classification}</dd>
          <dt>
            <span id="entryChannel">
              <Translate contentKey="gatewayApp.issue.entryChannel">Entry Channel</Translate>
            </span>
          </dt>
          <dd>{issueEntity.entryChannel}</dd>
          <dt>
            <Translate contentKey="gatewayApp.issue.category">Category</Translate>
          </dt>
          <dd>{issueEntity.category ? issueEntity.category.id : ''}</dd>
          <dt>
            <Translate contentKey="gatewayApp.issue.customer">Customer</Translate>
          </dt>
          <dd>{issueEntity.customer ? issueEntity.customer.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/issue" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/issue/${issueEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IssueDetail;
