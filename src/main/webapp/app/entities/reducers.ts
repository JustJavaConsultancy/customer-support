import category from 'app/entities/category/category.reducer';
import issue from 'app/entities/issue/issue.reducer';
import comment from 'app/entities/comment/comment.reducer';
import customer from 'app/entities/customer/customer.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  category,
  issue,
  comment,
  customer,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
