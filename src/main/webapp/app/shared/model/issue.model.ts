import dayjs from 'dayjs';
import { IComment } from 'app/shared/model/comment.model';
import { ICategory } from 'app/shared/model/category.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { ISSUESTATUS } from 'app/shared/model/enumerations/issuestatus.model';
import { CLASSIFICATION } from 'app/shared/model/enumerations/classification.model';
import { ENTRYCHANNEL } from 'app/shared/model/enumerations/entrychannel.model';

export interface IIssue {
  id?: number;
  createdDate?: string | null;
  description?: string | null;
  status?: ISSUESTATUS | null;
  classification?: CLASSIFICATION | null;
  entryChannel?: ENTRYCHANNEL | null;
  comments?: IComment[] | null;
  category?: ICategory | null;
  customer?: ICustomer | null;
}

export const defaultValue: Readonly<IIssue> = {};
