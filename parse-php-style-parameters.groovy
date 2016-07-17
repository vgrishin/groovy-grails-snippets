/*
 * PHP can parse POST-parameters like 'contacts[update][0][id]' into objects. 
 * I did not find such parser in groovy/grails, so I have writed my own one.
 * NOTICE: Pass object's name and parser will retrieve this object from params.
 */

def parsePhpPost(String itemName, def params) {
  if(!itemName || !params) return null

  def itemStructure = [:]

  def nameRegexp = ~"^${itemName}\\["
  def paramItems = params?.findResults { k, v ->
    if(k ==~ /${nameRegexp}.+/)
      return [name: k, value: v]
    else return null
  }

  paramItems?.each {
    def nameStructure = it?.name ? it.name.tokenize('][') : ''
    nameStructure?.reverse(true)

    def elStruct = it.value
    nameStructure?.each { n ->
      def backupEl = elStruct
      if(n ==~ /\d+/) {
        elStruct = []
        elStruct[n?.toInteger()] = backupEl
      } else {
        elStruct = [:]
        elStruct["$n"] = backupEl
      }
    }

    nameStructure?.reverse(true)
    itemStructure = processResult(nameStructure, elStruct, itemStructure)
  }

  return itemStructure
}

def processResult(List keys, def items, def itemStruct) {
  def current = keys?.size() ? keys[0] : null
  keys = keys?.size() > 1 ? keys[1..-1] : []

  if(current == null)
    return itemStruct;

  if(current ==~ /\d+/)
    current = current?.toInteger()

  if(!itemStruct) {
    if(current instanceof Integer)
      itemStruct = []
    else itemStruct = [:]
  }

  itemStruct[current] = keys?.size() ? processResult(keys, items[current], itemStruct[current]) : items[current]

  return itemStruct
}

/*
 * Usage example.
 */
 
def params = [
  'contacts[update][0][id]': 32346589,
  'contacts[update][0][name]': "Example",
  'contacts[update][0][responsible_user_id]': 664584,
  'contacts[update][0][date_create]': 1467022194,
  'contacts[update][0][last_modified]': 1467119238,
  'contacts[update][0][created_user_id]': 664584,
  'contacts[update][0][modified_user_id]': 664584,
  'contacts[update][0][company_name]': "Example",
  'contacts[update][0][linked_company_id]': 32346587,
  'contacts[update][0][custom_fields][0][id]': 916946,
  'contacts[update][0][custom_fields][0][name]': "Birthday",
  'contacts[update][0][custom_fields][0][values][0][value]': "15.06.2016",
  'contacts[update][0][custom_fields][1][id]': 916948,
  'contacts[update][0][custom_fields][1][name]': "Price",
  'contacts[update][0][custom_fields][1][values][0][value]': 1234,
  'contacts[update][0][custom_fields][2][id]': 916950,
  'contacts[update][0][custom_fields][2][name]': "Select",
  'contacts[update][0][custom_fields][2][values][0][value]': "Value One",
  'contacts[update][0][custom_fields][2][values][0][enum]': 2218698,
  'contacts[update][0][custom_fields][3][id]': 916952,
  'contacts[update][0][custom_fields][3][name]': "Checkbox",
  'contacts[update][0][custom_fields][3][values][0][value]': "1",
  'contacts[update][0][custom_fields][4][id]': 916954,
  'contacts[update][0][custom_fields][4][name]': "Created",
  'contacts[update][0][custom_fields][4][values][0][value]': "15.06.2016",
  'contacts[update][0][type]': "contact"
]

// Pass object's name 'contacts' and retrieve this object:
print parsePhpPost('contacts', params)
