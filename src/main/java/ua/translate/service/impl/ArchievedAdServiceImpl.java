package ua.translate.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ArchievedAdDao;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.service.ArchievedAdService;
import ua.translate.service.exception.InvalidIdentifier;

@Service
@Transactional
public class ArchievedAdServiceImpl implements ArchievedAdService {
	
	@Autowired
	private ArchievedAdDao archievedAdDao;
	
	@Override
	public Set<ArchievedAd> getAllArchievedAdsInDescOrder() {
		return archievedAdDao.getAllArchievedAdsInDescOrder();
	}

	@Override
	public ArchievedAd getArchievedAdByAdId(long id) throws InvalidIdentifier {
		ArchievedAd archievedAd = archievedAdDao.getArchievedAdByAdId(id);
		if(archievedAd == null){
			throw new InvalidIdentifier();
		}
		return archievedAd;
	}

}
