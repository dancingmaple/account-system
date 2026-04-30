import request from '@/utils/request'

export function getMenuList(params) {
  return request({
    url: '/v1/menus/tree',
    method: 'get',
    params
  })
}

export function getMenuTree() {
  return request({
    url: '/v1/menus/tree',
    method: 'get'
  })
}

export function getMenuDetail(id) {
  return request({
    url: `/v1/menus/${id}`,
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/v1/menus',
    method: 'post',
    data
  })
}

export function updateMenu(id, data) {
  return request({
    url: `/v1/menus/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/v1/menus/${id}`,
    method: 'delete'
  })
}