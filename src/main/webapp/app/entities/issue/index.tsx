import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Issue from './issue';
import IssueDetail from './issue-detail';
import IssueUpdate from './issue-update';
import IssueDeleteDialog from './issue-delete-dialog';

const IssueRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Issue />} />
    <Route path="new" element={<IssueUpdate />} />
    <Route path=":id">
      <Route index element={<IssueDetail />} />
      <Route path="edit" element={<IssueUpdate />} />
      <Route path="delete" element={<IssueDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IssueRoutes;
