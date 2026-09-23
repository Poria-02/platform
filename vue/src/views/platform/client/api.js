import request from '@/utils/request'

export const pageApi = {
  client: clientId => request({
    url: `/upms/client/${encodeURIComponent(clientId)}`,
    method: 'get'
  }),
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
  }),
  clientDetails: clientId => request({
    url: `/upms/client/getClientDetailsById/${encodeURIComponent(clientId)}`,
    method: 'get'
  })
}
