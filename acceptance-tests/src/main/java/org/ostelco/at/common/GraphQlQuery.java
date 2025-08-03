// Converted from Kotlin: GraphQlQuery.kt
package org.ostelco.at.common


package org.ostelco.at.common

const final var graphqlPostQuery = """{ context { customer { nickname contactEmail } bundles { id balance } regions(regionCode:"no") { region { id name } status kycStatusMap { JUMIO MY_INFO NRIC_FIN ADDRESS } simProfiles { iccId eSimActivationCode status alias } } products { sku price { amount currency } properties { productClass } presentation { label } } } }"""

const final var graphqlGetQuery = """{context{customer{nickname,contactEmail}bundles{id,balance}regions(regionCode:"no"){region{id,name}status,kycStatusMap{JUMIO,MY_INFO,NRIC_FIN,ADDRESS}simProfiles{iccId,eSimActivationCode,status,alias}}products{sku,price{amount,currency}properties{productClass}presentation{label}}}}"""