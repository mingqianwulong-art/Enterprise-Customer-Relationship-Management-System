import request from './request'

// ==================== 用户管理 ====================

/** 分页查询用户 */
export function getUserPage(params: any) {
  return request({ url: '/system/user/page', method: 'get', params })
}

/** 查询用户详情 */
export function getUserById(id: number) {
  return request({ url: `/system/user/${id}`, method: 'get' })
}

/** 新增用户 */
export function addUser(data: any) {
  return request({ url: '/system/user', method: 'post', data })
}

/** 修改用户 */
export function updateUser(data: any) {
  return request({ url: '/system/user', method: 'put', data })
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request({ url: `/system/user/${id}`, method: 'delete' })
}

/** 重置密码 */
export function resetPwd(userId: number, password: string) {
  return request({ url: '/system/user/resetPwd', method: 'put', params: { userId, password } })
}

/** 修改用户状态 */
export function changeUserStatus(userId: number, status: number) {
  return request({ url: '/system/user/changeStatus', method: 'put', params: { userId, status } })
}

/** 查询用户已分配的角色ID列表 */
export function getUserRoles(userId: number) {
  return request({ url: `/system/user/${userId}/roles`, method: 'get' })
}

/** 分配角色 */
export function assignUserRoles(userId: number, roleIds: number[]) {
  return request({ url: `/system/user/${userId}/roles`, method: 'put', data: { roleIds } })
}

// ==================== 角色管理 ====================

/** 分页查询角色 */
export function getRolePage(params: any) {
  return request({ url: '/system/role/page', method: 'get', params })
}

/** 查询角色详情 */
export function getRoleById(id: number) {
  return request({ url: `/system/role/${id}`, method: 'get' })
}

/** 新增角色 */
export function addRole(data: any) {
  return request({ url: '/system/role', method: 'post', data })
}

/** 修改角色 */
export function updateRole(data: any) {
  return request({ url: '/system/role', method: 'put', data })
}

/** 删除角色 */
export function deleteRole(id: number) {
  return request({ url: `/system/role/${id}`, method: 'delete' })
}

/** 查询所有角色（下拉选择用） */
export function getRoleList() {
  return request({ url: '/system/role/list', method: 'get' })
}

// ==================== 菜单管理 ====================

/** 查询菜单树 */
export function getMenuTree() {
  return request({ url: '/system/menu/tree', method: 'get' })
}

/** 查询菜单详情 */
export function getMenuById(id: number) {
  return request({ url: `/system/menu/${id}`, method: 'get' })
}

/** 新增菜单 */
export function addMenu(data: any) {
  return request({ url: '/system/menu', method: 'post', data })
}

/** 修改菜单 */
export function updateMenu(data: any) {
  return request({ url: '/system/menu', method: 'put', data })
}

/** 删除菜单 */
export function deleteMenu(id: number) {
  return request({ url: `/system/menu/${id}`, method: 'delete' })
}

// ==================== 部门管理 ====================

/** 查询部门树 */
export function getDeptTree() {
  return request({ url: '/system/dept/tree', method: 'get' })
}

/** 查询部门详情 */
export function getDeptById(id: number) {
  return request({ url: `/system/dept/${id}`, method: 'get' })
}

/** 新增部门 */
export function addDept(data: any) {
  return request({ url: '/system/dept', method: 'post', data })
}

/** 修改部门 */
export function updateDept(data: any) {
  return request({ url: '/system/dept', method: 'put', data })
}

/** 删除部门 */
export function deleteDept(id: number) {
  return request({ url: `/system/dept/${id}`, method: 'delete' })
}

// ==================== 操作日志 ====================

/** 分页查询操作日志 */
export function getLogPage(params: any) {
  return request({ url: '/system/log/page', method: 'get', params })
}
