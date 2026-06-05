export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  token: string;
  role: string;
  name: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phone: string;
}

export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  active: boolean;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  phone: string;
  role: string;
}

export interface Product {
  productId?: number;
  name: string;
  category: string;
  unit: string;
  price: number;
  status: string;
}

export interface Supplier {
  supplierId?: number;
  name: string;
  contactInfo: string;
  status: string;
}

export interface SupplierOrder {
  orderId?: number;
  supplierId: number;
  productIdsJson: string;
  quantity: number;
  orderedAt?: string;
  status: string;         
  receivedByUserId?: number;
  receivedAt?: string;
}

export interface ReceiveOrder {
  warehouseId: number;
  quantityReceived: number;
}

export interface Department {
  departmentId?: number;
  name: string;
  headId?: number;
  contactInfo: string;
  status: string;
}

export interface DepartmentRequestCreate {
  departmentId: number;
  productIds: number[];
  quantity: number;
}

export interface DepartmentRequest {
  requestId: number;
  departmentId: number;
  productIdsJson: string;
  quantity: number;
  status: string;
  requestedAt: string;
  approvedBy?: string;
  createdBy?: string;
  createdByUserId?: number;
  department?: Department;
}

export interface DeliveryCreate {
  requestId: number;
  quantity: number;
  warehouseId: number;
}

export interface Delivery {
  deliveryId: number;
  requestId: number;
  deliveredBy: string;
  deliveredAt: string;
  quantity: number;
  status: string;
  warehouseId?: number;
}

export interface ProofOfReceiptCreate {
  deliveryId: number;
  departmentId: number;
  fileUri: string;
}

export interface ProofOfReceipt {
  proofId: number;
  deliveryId: number;
  departmentId: number;
  receivedAt: string;
  fileUri: string;
  status: string;
}

export interface Invoice {
  invoiceId?: number;
  requestId?: number;
  departmentId: number;
  amount: number;
  issuedAt?: string;
  status: string;
  fileUri?: string;
}

export interface Payment {
  paymentId?: number;
  invoiceId: number;
  amount: number;
  method: string;
  paidAt?: string;
  status?: string;
}

export interface Warehouse {
  warehouseId?: number;
  name: string;
  location: string;
  capacity: number;
  active?: boolean;
}

export interface InventoryItem {
  itemId?: number;
  warehouse?: Warehouse;
  productId: number;
  quantity: number;
  receivedAt?: string;
  status?: string;
}

export interface InventoryCreate {
  warehouseId: number;
  productId: number;
  quantity: number;
}

export interface RestockCreate {
  warehouseId: number;
  productId: number;
  quantity: number;       
  sourceOrderId?: number; 
}

export interface StockMovement {
  movementId?: number;
  itemId: number;
  fromWarehouseId: number;
  toWarehouseId: number;
  quantity: number;
  performedAt?: string;
  status?: string;
}

export interface Notification {
  id: number;
  userId: number;
  referenceId?: number;
  message: string;
  category: string;
  status: string;
  createdAt: string;
}

export interface KPI {
  kpiID?: number;
  name: string;
  definition: string;
  target: string;
  currentValue: string;
  reportingPeriod: string;
  category: string;
}

export interface Report {
  reportId?: number;
  scope: string;
  parametersJSON?: string;  
  metricsJSON?: string;      
  generatedBy?: string;
  generatedAt?: string;
  reportUri?: string;
  fileName?: string;
}

export interface AuditPackage {
  packageId?: number;
  periodStart: string;
  periodEnd: string;
  contentsJSON?: string;    
  generatedAt?: string;
  packageUri?: string;
}

export interface ApiResponse<T> {
  message: string;
  data: T;
}

export interface PurchaseRequestCreate {
  productId: number;
  quantity: number;
  departmentRequestId?: number;
  warehouseId?: number;
  notes?: string;
}

export interface PurchaseRequest {
  purchaseRequestId?: number;
  productId: number;
  quantity: number;
  departmentRequestId?: number;
  warehouseId?: number;
  createdByUserId?: number;
  notes?: string;
  status: string;           
  createdAt?: string;
  orderId?: number;        
  reviewedByUserId?: number;
  reviewedAt?: string;
}

export interface ApprovePurchaseRequest {
  supplierId: number;
}

export interface AuditLog {
  id: number;
  userId: number;
  action: string;
  resourceType: string;
  resourceId: string;
  details: string;
  timestamp: string;
}