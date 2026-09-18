import request from '@/utils/request'

export const pageApi = {
  clients: params => request({
    url: '/upms/client/page',
    method: 'get',
    params
  }),
  saveClient: data => request({
    url: '/upms/client',
    method: 'post',
    data
  }),
  updateClient: data => request({
    url: '/upms/client',
    method: 'put',
    data
  }),
  deleteClient: clientId => request({
    url: `/upms/client/${clientId}`,
    method: 'delete'
  }),
  clearClientCache: () => request({
    url: '/upms/client/cache',
    method: 'delete'
  })
}
